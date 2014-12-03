(ns castra.api
  (:require [clojure.tools.nrepl.server      :refer [start-server stop-server]])
  (:require [castra.rules :refer [allow deny]])
  (:require [monger.core :as mg])
  (:require [monger.collection :as mc])
  (:require [castra.config :as c])
  (:require [geo-cache.cache :as ca :refer [shahash]])
  (:require [geo-cache.mongo-cache :as m])
  (:require [geo-graph.graph :refer [vertices->edges]])
  (:require [tsp.capacity-cluster :as cc])
  (:require [geo-graph.google :as graph])
  (:require [tailrecursion.castra :refer [defrpc]])
  (:require [tsp.ant-colony :as ac]))

(def fields ["id" "label" "address" "lat" "lng"])

(def cache  (delay (ca/get-cache {:type :mongo 
                                  :uri c/mongo-uri 
                                  :db c/db 
                                  :edge c/edge-coll 
                                  :address c/address-coll})))

(defn get-depots []
  (do
    (when (m/cache-ready? @cache) 
      (mapv #(dissoc % :_id) 
            (mc/find-maps 
              (:db @(:conn @cache)) c/address-coll {:type "depot"} fields)))))

(defn get-stops []
  (do 
    (when (m/cache-ready? @cache) 
      (mapv #(dissoc % :_id) 
            (mc/find-maps 
              (:db @(:conn @cache)) c/address-coll {:type "stop"} fields)))))

 
(defn get-polyline [from to]
  (let [id     (shahash from to)]
    (when (m/cache-ready? @cache) 
      (:points 
        (dissoc 
          (mc/find-one-as-map 
            (:db @(:conn @cache)) c/edge-coll {:id id} ["points"]) 
          :_id)))))

(defn tour-stops [stops vertices]
  (mapv #(mapv (fn[x] (nth stops x)) %) (vertices->edges vertices)))

(defn tour-polylines [ts]
  (mapv #(apply get-polyline %) ts))

(defn route
  [vs stops]
  (mapv #(:id (nth stops %)) vs))

(defn get-route [stops]
  (let [g       (graph/concurrent-google-graph stops  @cache)
        c       (ac/make-ant-colony-solver-config nil)
        sol     (ac/make-ant-colony-solution g c)
        s       (ac/ant-colonies sol 10) 
        ts      (tour-stops stops (:tour s))
        p       (tour-polylines ts)]
    (assoc {} 
           :route (route (:tour s) stops) 
           :polylines p 
           :distance (:distance s) 
           :duration (:duration s))))

(defn get-clusters [ps]
  (let [s        (:stops ps)
        d        (:depot ps)
        t        (java.lang.Integer/parseInt (:trucks ps))
        cap      (java.lang.Integer/parseInt (:capacity ps))
        stops    (mapv #(assoc % :bearing (cc/bearing d %) :load 1 ) s)
        trucks   (mapv #(assoc {} :id (inc %) :capacity cap :status "available" :load 0 ) (range t))
        s        (cc/cluster {:trucks trucks :stops stops})]
    (mapv #(dissoc % :bearing :load) (:stops s))))


(defn get-routes* [ps]
  (let [start#   (. System (currentTimeMillis))  
        clusters (get-clusters ps)
        d        (:depot ps)
        t        (java.lang.Integer/parseInt (:trucks ps))
        rts      (into [] (map #(filter (fn [x] (= (:truck x) (inc %))) clusters) (range t)))
        sols     (into [] (pmap #(get-route (cons d %)) rts))
        t        (format "%,5.2f" (/ (double (- (. System (currentTimeMillis)) start#)) 1000.0))
        tstr     (str "Elapsed time: " t " seconds")]
   {:trucks (:trucks ps) :capacity (:capacity ps) :routes sols :time tstr}))

(defrpc get-routes [ps]
  {:rpc [(allow)]}
  (get-routes* ps))

(defrpc get-state [] 
  {:rpc [(allow)]}
  {:stops (get-stops) :depots (get-depots)})
 

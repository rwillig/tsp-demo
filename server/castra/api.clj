(ns castra.api
  (:require [castra.rules :refer [allow deny]])
  (:require [taoensso.faraday :as far])
  (:require [castra.config :as c])
  (:require [geo-cache.cache :as ca :refer [shahash]])
  (:require [geo-cache.redis-cache :as rc])
  (:require [geo-graph.graph :refer [vertices->edges intra]])
  (:require [tsp.capacity-cluster :as cc])
  (:require [geo-graph.google :as graph])
  (:require [tailrecursion.castra :refer [defrpc]])
  (:require [tsp.ant-colony :as ac]))

(def fields ["id" "label" "address" "lat" "lng"])

(def dyndb-creds  {:access-key c/dynamo-access
                   :secret-key c/dynamo-secret})

(def cache        (ca/get-cache {:type :redis}))

(defn get-depots []
  (far/scan dyndb-creds :location {:attr-conds {:type [:eq "depot"]}}))

(defn get-stops []
  (far/scan dyndb-creds :location {:attr-conds {:type [:eq "stop"]}}))

 
(defn get-polyline [from to]
  (let [id     (shahash from to)]
    (:points (rc/get-weight from to))))

(defn tour-edges [stops vertices]
  (mapv #(mapv (fn[x] (nth stops x)) %) (vertices->edges vertices)))

(defn tour-polylines [ts]
  (mapv #(apply get-polyline %) ts))

(defn route
  [vs stops]
  (mapv #(:id (nth stops %)) vs))

(defn get-route [stops]
  (let [g       (graph/concurrent-google-graph stops  cache)
        f       (filter #(not (nil? (:constraint %))) stops)
        m       (map #(assoc {} (.indexOf stops %) (:constraint %)) f)
        cs      (into {} m) 
        c       (ac/make-ant-colony-solver-config 
                  (when (seq cs) {:constraint :time-window}))
        sol     (if (seq cs) 
                  (ac/make-constrained-solution g c cs)
                  (ac/make-ant-colony-solution g c))
        s       (ac/ant-colonies sol 4) 
        idis    (intra g :distance (vertices->edges (:tour s)))
        idur    (intra g :duration (vertices->edges (:tour s)))
        ts      (tour-edges stops (:tour s))
        u       (mapv #(assoc {} 
                          :id (:id (nth stops (key %))) 
                          :constraint (val %)) 
                      (:unmet s))
        p       (tour-polylines ts)]
    (assoc {} 
           :depot (route (vector (:depot s)) stops)
           :stops (route (:stops s) stops)
           :terminal (route (vector (:terminal s)) stops)
           :unmet  u
           :intra {:distance idis :duration idur}
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
        ts       (count (distinct (mapv :truck clusters)))
        t        (java.lang.Integer/parseInt (:trucks ps))
        t        (min t ts)
        rts      (into [] (map #(filter (fn [x] (= (:truck x) (inc %))) clusters) (range t)))
        sols     (into [] (pmap #(get-route (cons d %)) rts))
        t        (format "%,5.2f" (/ (double (- (. System (currentTimeMillis)) start#)) 1000.0))
        tstr     (str "Elapsed time: " t " seconds")]
   {:trucks (:trucks ps) :capacity (:capacity ps) :routes sols :time tstr}))

(defrpc get-routes [ps]
  {:rpc [(allow)]}
  (get-routes* ps))

(defrpc test-cache
  []
  {:rpc/prei [(allow)]}
  (let [stops  (get-stops)
        a      (-> stops first :address)]
  (rc/get-weight (first stops) (second stops))))

(defrpc get-state [] 
  {:rpc [(allow)]}
  {:stops (get-stops) :depots (get-depots)})
 
(ns tspdemo.api.demo
  (:refer-clojure :exclude [defn])
  (:require [tspdemo.http.rules :refer [allow deny]])
  (:require [monger.core :as mg])
  (:require [monger.collection :as mc])
  (:require [tspdemo.config :as c])
  (:require [tspSolver.cache :refer [shahash]])
  (:require [tspSolver.graph :refer [vertices->edges]])
  (:require [tspSolver.capacity-cluster :as cc])
  (:require [tspSolver.google :as graph])
  (:require [tspdemo.http.rules :refer :all])
  (:require [tailrecursion.castra :refer [defn]])
  (:require [tspSolver.ant-colony :as ac]))

(def fields ["id" "label" "address" "lat" "lng"])

(defn mongo-connection []
  (if 
	  (and (c/mongo-instance?) (c/mongo-open?))
	  mg/*mongodb-connection* (mg/connect-via-uri! c/mongo-uri)))

(defn get-depots []
  (let [mongo (mongo-connection)
        mdb    (mg/use-db! c/db)]
    (mapv #(dissoc % :_id) (mc/find-maps c/address-coll {:type "depot"} fields))))

(defn get-stops []
  (let [mongo (mongo-connection)
        mdb    (mg/use-db! c/db)]
    (mapv #(dissoc % :_id) (mc/find-maps c/address-coll {:type "stop"} fields))))

(defn get-state [] 
  {:rpc [(allow)]}
  {:stops (get-stops) :depots (get-depots)})
  
(defn get-polyline [from to]
  (let [mongo  (mongo-connection)
        mdb    (mg/use-db! c/db)
        id     (shahash from to)]
    (:points (dissoc (mc/find-one-as-map c/edge-coll {:id id} ["points"]) :_id))))

(defn tour-stops [stops vertices]
  (mapv #(mapv (fn[x] (nth stops x)) %) (vertices->edges vertices)))

(defn tour-polylines [ts]
  (mapv #(apply get-polyline %) ts))

(defn route
  [vs stops]
  (mapv #(:id (nth stops %)) vs))

(defn get-route [stops]
  (let [g       (graph/concurrent-google-graph stops "distance")
        c       (ac/make-ant-colony-solver-config nil)
        sol     (ac/make-ant-colony-solution g 0 c)
        s       (select-keys (ac/ant-colonies sol) [:vertices :trip])
        ts      (tour-stops stops (:vertices s))
        p       (tour-polylines ts)]
    {:route (route (:vertices s) stops) :polylines p :trip (:trip s)}))

(defn get-clusters [ps]
  (let [s        (:stops ps)
        d        (:depot ps)
        t        (java.lang.Integer/parseInt (:trucks ps))
        cap      (java.lang.Integer/parseInt (:capacity ps))
        stops    (mapv #(assoc % :bearing (cc/bearing d %) :load 1 ) s)
        trucks   (mapv #(assoc {} :id (inc %) :capacity cap :status "available" :load 0 ) (range t))
        s        (cc/cluster {:trucks trucks :stops stops})]
    (mapv #(dissoc % :bearing :load) (:stops s))))

(defn timed [f & args]
  (let [out (atom nil)
        sec (with-out-str (reset! out (apply f args)))]
    {:result @out :time sec}))

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

(defn get-routes** [ps]
  {:rpc [(allow)]}
  (let [{:keys [result time]} (timed get-routes* ps)]
    (assoc result :time time)))
(defn get-routes [ps]
  {:rpc [(allow)]}
  (get-routes* ps))


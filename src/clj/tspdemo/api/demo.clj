(ns tspdemo.api.demo
  (:refer-clojure :exclude [defn])
  (:require [tspdemo.http.rules :refer :all])
  (:require [monger.core :as mg])
  (:require [monger.collection :as mc])
  (:require [tspdemo.config :as c])
  (:require [tspSolver.cache :refer [shahash]])
  (:require [tspSolver.graph :refer [vertices->edges]])
  (:require [tspSolver.google :as graph])
  (:require [tspdemo.http.rules :refer :all])
  (:require [tailrecursion.castra :refer [defn]])
  (:require [tspSolver.ant-colony :as ac]))

(def fields ["id" "label" "address" "lat" "lng"])

(defn mongo-connection []
  {:rpc [(allow)]}
  (println *out* c/mongo-uri)
  (if 
	  (and (c/mongo-instance?) (c/mongo-open?))
	  mg/*mongodb-connection* (mg/connect-via-uri! c/mongo-uri)))

(defn get-depots []
  {:rpc [(allow)]}
  (let [mongo (mongo-connection)
        mdb    (mg/use-db! c/db)]
    (mapv #(dissoc % :_id) (mc/find-maps c/address-coll {:type "depot"} fields))))

(defn get-stops []
  {:rpc [(allow)]}
  (let [mongo (mongo-connection)
        mdb    (mg/use-db! c/db)]
    (mapv #(dissoc % :_id) (mc/find-maps c/address-coll {:type "stop"} fields))))

(defn get-state [] {:rpc [(allow)]}{:stops (get-stops) :depots (get-depots)})
  
(defn get-polyline [from to]
  {:rpc [(deny)]}
  (let [mongo  (mongo-connection)
        mdb    (mg/use-db! c/db)
        id     (shahash from to)]
    (:points (dissoc (mc/find-one-as-map c/edge-coll {:id id} ["points"]) :_id))))

(defn tour-stops [stops vertices]
  {:rpc [(deny)]}
  (mapv #(mapv (fn[x] (nth stops x)) %) (vertices->edges vertices)))

(defn tour-polylines [ts]
  {:rpc [(deny)]}
  (mapv #(apply get-polyline %) ts))
(defn route
  [vs stops]
  (mapv #(:id (nth stops %)) vs))

(defn get-route [stops]
  {:rpc [(allow)]}
  (let [g       (graph/google-graph stops "distance")
        s       (select-keys (ac/run g 0) [:vertices :trip])
        ts      (tour-stops stops (:vertices s))
        p       (tour-polylines ts)]
    {:route (route (:vertices s) stops) :polylines p :trip (:trip s)}))

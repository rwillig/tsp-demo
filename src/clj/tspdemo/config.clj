(ns tspdemo.config
  (:require [monger.core :as mg])
  (:import [org.bson.types ObjectId]
           [com.mongodb DB WriteConcern MongoClient]))

(def mongo-uri (System/getenv "MONGO_URI"))
(def db (System/getenv "MONGO_DB"))
(def address-coll (System/getenv "MONGO_ADDRESS_COLLECTION"))
(def edge-coll (System/getenv "MONGO_EDGE_COLLECTION"))
(def port (System/getenv "PORT"))

(defn mongo-instance? []
  (instance? com.mongodb.MongoClient monger.core/*mongodb-connection*))

(defn mongo-open? []
  (.. monger.core/*mongodb-connection* (getConnector) (isOpen)))

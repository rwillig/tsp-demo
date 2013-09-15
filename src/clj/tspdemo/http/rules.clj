(ns tspdemo.http.rules
  (:require
    [tailrecursion.castra :refer [ex auth *request* *session*]]))

(def allow (constantly true))
(def deny  #(throw (ex auth "Fuck off and Die.")))


(ns ctest.api.foo
  (:refer-clojure :exclude [defn])
  (:require
    [ctest.http.rules      :refer :all]
    [tailrecursion.castra  :refer [defn]]))

(defn login [user pass]
  {:rpc [(login! user pass)]
   :pre [(not= user "omfg")]}
  "Congratulations, you're logged in.")

(defn logout []
  {:rpc [(logout!)]}
  "Congratulations, you're logged out.")

(def data (take 100000 (map #(hash-map (+ (rand-int %) 0.256) (rand-int %)) (cycle [1000]))))

(defn test0 []
  {:rpc [(allow)]}
  data)

(defn test1 [x y]
  {:rpc [(logged-in?)]}
  (+ x y))

(defn test2 [x y]
  {:rpc [(deny)]}
  (- x y))

(defn test3
  "Calls test2 without triggering 'deny' assertion."
  [x y]
  (test2 x y))

(defn ^:private test4
  "Not accessible via RPC (not public)."
  [x y]
  {:rpc [(allow)]}
  (* x y))

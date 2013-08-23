(ns tsp-demo.core
  (:require
    [ring.adapter.jetty              :refer [run-jetty]]
    [ring.middleware.resource        :refer [wrap-resource]]
    [ring.middleware.session         :refer [wrap-session]]
    [ring.middleware.session.cookie  :refer [cookie-store]]
    [ring.middleware.file            :refer [wrap-file]]
    [ring.middleware.file-info       :refer [wrap-file-info]]
    [tailrecursion.castra.handler    :refer [castra]]))

(def app
  (->
    (castra '[clojure.core :only [inc]] 'tsp-demo.api.demo)
    (wrap-session {:store (cookie-store {:key "a 16-byte secret"})})
    (wrap-resource "public")
    (wrap-file-info)))

(defonce server (run-jetty #'app {:join? false :port 3000}))

(defn -main
  "I don't do a whole lot."
  [& args])
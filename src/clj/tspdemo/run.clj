(ns tspdemo.run
  (:gen-class)
  (:require
    [clojure.pprint                  :refer [pprint]]
    [ring.adapter.jetty              :as jetty]
    ;[compojure.route                 :as route]
    [ring.middleware.resource        :refer [wrap-resource]]
    [ring.middleware.session         :refer [wrap-session]]
    [ring.middleware.session.cookie  :refer [cookie-store]]
    [ring.middleware.file            :refer [wrap-file]]
    [ring.middleware.index-file      :refer [wrap-index-paths]]
    [ring.middleware.file-info       :refer [wrap-file-info]]
    [tailrecursion.castra.handler    :refer [castra]]))


(comment (defn get-app [servlet]
  ;(pprint servlet)
  (let [app (->
    (castra '[clojure.core :only [inc]] 'tspdemo.api.demo)
    (wrap-session {:store (cookie-store {:key "a 16-byte secret"})})
    (wrap-index-paths "public")    
    ;(wrap-resource "public")
    ;(wrap-file "public" {:index-files? true})
    (wrap-file-info))] app )))


(def app
  (->
    (castra '[clojure.core :only [inc]] 'tspdemo.api.demo)
    (wrap-session {:store (cookie-store {:key "a 16-byte secret"})})
    (wrap-resource "public")    
    (wrap-index-paths "index.html")    
    (wrap-file-info)))

;(defonce server (jetty/run-jetty #'app {:join? false :port 3000}))
(defn -main
  "I don't do a whole lot."
  [& args]
  (let [app app] (jetty/run-jetty app {:join? false :port 3000})))

  


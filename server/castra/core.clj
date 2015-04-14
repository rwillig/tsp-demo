(ns castra.core
  (:require
    [ring.adapter.jetty              :as jetty]
    [ring.middleware.resource        :refer [wrap-resource]]
    [ring.middleware.session         :refer [wrap-session]]
    [ring.middleware.session.cookie  :refer [cookie-store]]
    [ring.middleware.file            :refer [wrap-file]]
    [ring.middleware.cors            :refer [wrap-cors]]
    [tailrecursion.castra.handler    :refer [castra]]))


(def app
  (->
    (castra             'castra.api)
    
    (wrap-session       {:store (cookie-store {:key "a 16-byte secret"})})

    (wrap-resource      (or 
                          (System/getProperty "resource-root")
                          (System/getenv "resource-root")
                          "/"))
;
    (wrap-file          (or 
                          (System/getProperty "file-root")
                          (System/getenv "file-root")
                          "/"))

    (wrap-cors          identity)))

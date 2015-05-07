(set-env!
  :dependencies  '[[adzerk/boot-beanstalk                   "0.2.3"           :scope "test"]
                   [adzerk/boot-cljs                        "0.0-2814-4"      :scope "test"]
                   [adzerk/boot-reload                      "0.2.4"           :scope "test"]
                   [pandeiro/boot-http                      "0.6.1"           :scope "test"]
                   [cljsjs/boot-cljsjs                      "0.4.6"           :scope "test"]
                   [tailrecursion/boot-hoplon               "0.1.0-SNAPSHOT"  :scope "test"]
                   [tailrecursion/hoplon                    "6.0.0-SNAPSHOT"]
                   [cljsjs/moment                           "2.9.0-0"]
                   [cljsjs/jquery-ui                        "1.11.3-1"]
                   [jumblerg/ring.middleware.cors           "1.0.1"]
                   [tailrecursion/castra                    "3.0.0"]
                   [ring                                    "1.3.1"]
                   [raywillig/geo-cache                     "0.0.7"]
                   [raywillig/geo-graph                     "0.0.6"]
                   [com.taoensso/timbre                     "3.4.0"]
                   [com.taoensso/faraday                    "1.5.0"]
                   [tsp                                     "2.1.3"]
                   [hoplon/google-maps                      "3.18.0"]
                   [hoplon/twitter-bootstrap                "0.1.0"]
                   ]
  :source-paths   #{"src"}
  :resource-paths #{"assets"}
  :target-path    "resources")

(require
  '[adzerk.boot-cljs            :refer [cljs]]
  '[adzerk.boot-beanstalk       :refer [beanstalk dockerrun]]
  '[adzerk.boot-reload          :refer [reload]]
  '[pandeiro.boot-http          :refer [serve]]
  '[cljsjs.boot-cljsjs          :refer [from-cljsjs]]
  '[tailrecursion.boot-hoplon   :refer [hoplon prerender]])

(task-options!
  web          {:serve           'castra.core/app
                :create          'castra.config/init-server}
  beanstalk    {:access-key      (System/getenv "AWS_KEY")
                :secret-key      (System/getenv "AWS_PASS")
                :name            "tsp-demo"
                :version         "0.1.0-SNAPSHOT"
                :description     "rowtr.co tsp demo"
                :beanstalk-envs  [{:name         "tsp-demo"
                                    :cname-prefix "tsp-demo"}
                                  {:name "tsp-demo-dev"
                                   :cname-prefix "tsp-demo-dev"}]
                :stack-name      "64bit Amazon Linux 2014.09 v1.2.0 running Tomcat 7 Java 7"})

(deftask env 
  "set up environments"
  [ l local               bool "local development"
    d dev                 bool "cors dev (use with c)"
    c cors                bool "cors"
    e elastic-beanstalk   bool "elastic beanstalk deployment"]
  (when local 
    (do
      (set-env! :resource-paths #(conj % "server"))
      (System/setProperty "castra-url" "//localhost:8000")
      (System/setProperty "resource-root" "/")
      (System/setProperty "file-root" "resources")))
  (when cors
    (do
      (System/setProperty "castra-url" "//castra.rowtr.co"))
    (when dev
      (System/setProperty "castra-url" "//castra-dev.rowtr.co")))
  (when elastic-beanstalk
    (do
      (set-env! :resource-paths #(conj % "server")))))


(deftask dev
  "dev task using boot-http"
  []
  (comp 
    (watch)
    (hoplon :pretty-print true )
    (prerender)
    (reload)
    (cljs :source-map true :optimizations :none :unified-mode true)
    (from-cljsjs :profile :development)
    (sift :to-resource #{#"images" #"\.inc\.css"})
    (serve  :handler 'castra.core/app :port 8000)
    (speak)))

(deftask prod
  "dev task using boot-http"
  []
  (comp 
    (watch)
    (hoplon :pretty-print true )
    (prerender)
    (reload)
    (cljs :optimizations :advanced)
    (from-cljsjs :profile :production)
    (sift :to-resource #{#"images" #"\.inc\.css"})
    (serve  :handler 'castra.core/app :port 8000)
    (speak)))

(deftask development
  "compile only dev version"
  []
  (comp
    (hoplon :pretty-print true )
    (cljs :optimizations :none :unified-mode true :source-map true)))

(deftask production
  "Build for production deployment."
  []
  (comp
    (hoplon)
    (cljs :optimizations :advanced)))

(deftask tomcat-dev
  "build tomcat dev"
  []
  (set-env! :resource-paths #(conj % "server"))
  (set-env! :target-path "target")
  (comp
    (development)
    (web)
    (uber :as-jars true)
    (war)))

(deftask build-tomcat 
  "build tomcat package"
  []
  (set-env! :resource-paths #(conj % "server"))
  (set-env! :target-path "target")
  (comp
    (prod)
    (web)
    (uber :as-jars true)
    (war)))

(set-env!
  :dependencies  '[[adzerk/boot-cljs                        "0.0-2727-0" :scope "test"]
                   [adzerk/boot-cljs-repl                   "0.1.8"      :scope "test"]
                   [adzerk/boot-reload                      "0.2.4"      :scope "test"]
                   [pandeiro/boot-http                      "0.6.1"      :scope "test"]
                   [tailrecursion/boot-ring                 "0.1.0"      :scope "test"]
                   [rwillig/boot-castra                     "0.1.0-SNAPSHOT" :scope "test"]
                   [tailrecursion/hoplon                    "6.0.0-SNAPSHOT"]
                   [cljsjs/moment                           "2.9.0-0"]
                   [tailrecursion/castra                    "3.0.0"]
                   [ring                                    "1.3.1"]
                   [tsp                                     "2.1.1"]
                   [raywillig/ring-middleware-index-file    "1.0.7"]
                   [hoplon/google-maps                      "3.18.0"]
                   [hoplon/twitter-bootstrap                "0.1.0"]]
  :source-paths   #{"src/clj" "src/cljs" "src/html"}
  :asset-paths    #{"assets"}
  :target-path    "resources/public")

(require
  '[adzerk.boot-cljs :refer [cljs]]
  '[adzerk.boot-cljs-repl :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload :refer [reload]]
  '[pandeiro.boot-http :refer [serve]]
  '[tailrecursion.boot-ring  :refer [dev-server]]
  '[rwillig.boot-castra :refer [castra-dev-server]]
  '[tailrecursion.hoplon.boot :refer [hoplon]])

(deftask pandeiro
  "dev task using boot-http"
  []
  (comp 
    (watch)
    (hoplon :pretty-print true)
    (cljs :optimizations :none :unified-mode true)
    (serve :dir (get-env :target-path) :handler 'castra.core/app)
    (speak)))

(deftask castra-dev
  "Build for local development."
  []
  (comp
    (watch)
    (hoplon :pretty-print true)
    (cljs :optimizations :none :unified-mode true)
    (castra dev-server :namespaces 'castra.api)
    (speak)))

(deftask dev
  "Build for local development."
  []
  (comp
    (watch)
    (hoplon :pretty-print true)
    (cljs :optimizations :none :unified-mode true)
    (dev-server)
    (speak)))

(deftask prod
  "Build for production deployment."
  []
  (comp
    (watch)
    (hoplon)
    (cljs :optimizations :advanced)
    ;(serve :dir (get-env :target-path))
    (speak)))

#!/usr/bin/env boot

#tailrecursion.boot.core/version "2.5.0"


(set-env!
  :dependencies '[ [tailrecursion/hoplon                    "5.10.24"]
                   [io.hoplon/google.jsapi                  "0.3.5"]
                   [ring                                    "1.3.1"]
                   [org.clojure/tools.nrepl                 "0.2.5"]
                   [raywillig/ring-middleware-index-file    "1.0.7"]
                   [tailrecursion/boot.task                 "2.2.4"]
                   [tailrecursion/boot.notify               "2.0.2"]
                   [tailrecursion/boot.ring                 "0.2.1"]
                   [tsp                                     "2.0.4"]
                  ]
  :out-path     "resources/public"
  :src-paths    #{"src" "src/clj" })

(require
  '[tailrecursion.hoplon.boot      :refer :all]
  '[tailrecursion.boot.task.notify :refer [hear]]
  '[tailrecursion.castra.task :as c]
  '[tailrecursion.boot.task.ring   :refer [dev-server]])

(add-sync! (get-env :out-path) #{"assets"})

(deftask development
  "Build project for development, local dev server."
  []
  (comp (watch) (hear) (hoplon {:pretty-print true :source-map true :prerender true}) (c/castra-dev-server 'castra.api)))

(deftask production
  "Build project for production."
  []
  (hoplon {:optimizations :advanced}))

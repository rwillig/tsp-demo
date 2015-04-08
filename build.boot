(set-env!
  :dependencies  '[[adzerk/boot-cljs                        "0.0-2727-0"  :scope "test"]
                   [adzerk/boot-cljs-repl                   "0.1.8"       :scope "test"]
                   [adzerk/boot-reload                      "0.2.4"       :scope "test"]
                   [pandeiro/boot-http                      "0.6.1"       :scope "test"]
                   [cljsjs/boot-cljsjs                      "0.4.6"       :scope "test"]
                   [tailrecursion/boot-hoplon               "0.1.0-SNAPSHOT"]
                   [tailrecursion/hoplon                    "6.0.0-SNAPSHOT"]
                   [cljsjs/moment                           "2.9.0-0"]
                   [cljsjs/jquery-ui                        "1.11.3-1"]
                   [tailrecursion/castra                    "3.0.0"]
                   [com.taoensso/faraday                    "1.5.0"]
                   [ring                                    "1.3.1"]
                   [tsp                                     "2.1.1"]
                   [raywillig/ring-middleware-index-file    "1.0.7"]
                   [hoplon/google-maps                      "3.18.0"]
                   [hoplon/twitter-bootstrap                "0.1.0"]]
  :source-paths   #{"src"}
  :asset-paths    #{"assets"}
  :target-path    "resources/public")

(require
  '[adzerk.boot-cljs            :refer [cljs]]
  '[adzerk.boot-cljs-repl       :refer [cljs-repl start-repl]]
  '[adzerk.boot-reload          :refer [reload]]
  '[pandeiro.boot-http          :refer [serve]]
  '[cljsjs.boot-cljsjs          :refer [from-cljsjs]]
  '[tailrecursion.boot-hoplon   :refer [hoplon]])

(deftask dev
  "dev task using boot-http"
  []
  (comp 
    (watch)
    (hoplon :pretty-print true :source-maps true)
    (cljs :optimizations :none :unified-mode true)
;   (from-cljsjs :profile :development)
;   (sift :to-resource #{#"images" #"\.inc\.css"})
    (serve  :handler 'castra.core/app :port 8000)
    (speak)))

(deftask prod
  "Build for production deployment."
  []
  (comp
    (hoplon)
    (cljs :optimizations :advanced)))

(defproject tsp-demo "2.0.0"
  :description  "FIXME: add description"
  :url          "http://example.com/FIXME"
  :license      {:name  "Eclipse Public License"
                 :url   "http://www.eclipse.org/legal/epl-v10.html"}
  :min-lein-version "2.0.0"
  :plugins  [[lein-ring "0.8.13"]
             [s3-wagon-private "1.1.2"]]
  :repositories  {"private"  {:url "s3p://rsw-hdfs/releases/" 
                                                              :passphrase :env 
                                                              :username :env}}
  :uberjar-name "tsp-demo-standalone.jar"
  :dependencies [  [org.clojure/clojure                   "1.6.0"]
                   [tailrecursion/hoplon                  "5.10.24"]
                   [io.hoplon/twitter.bootstrap           "0.1.0"]
                   [io.hoplon/google.jsapi                "0.3.5"]
                   [ring                                  "1.3.1"]
                   [org.clojure/tools.nrepl               "0.2.5"]
                   [raywillig/ring-middleware-index-file  "1.0.7"]
                   [tsp                                   "2.0.4"]
                  ]
                
  :source-paths ["src/clj" "src/html" "src/cljs"]
  :war-resources-path "public"
  :ring         {:handler castra.core/app })

(defproject tsp-demo "0.1.0-SNAPSHOT"
  :description  "FIXME: add description"
  :url          "http://example.com/FIXME"
  :license      {:name  "Eclipse Public License"
                 :url   "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in :leiningen
  :plugins      [[tailrecursion/hoplon "0.1.0-SNAPSHOT"]
                 [s3-wagon-private "1.1.2"]]
  :main tsp-demo.core
  :min-lein-version "2.0.0"
  :uberjar-name "tsp-demo-standalone.jar"
  :repositories [["private" {:url "s3://tsprepo" :username :env :passphrase :env}]]  
  :dependencies [[org.clojure/clojure     "1.5.1"]
                 [tsp "1.1.2-SNAPSHOT"]
                 [alandipert/interpol8 "0.0.3"]
                 [ring "1.2.0"]
                 [compojure "1.1.5"]
                 [net.java.dev.jets3t/jets3t "0.7.4"]
                 [tailrecursion/castra    "0.1.0-SNAPSHOT"]
                 [tailrecursion/hoplon "1.1.0-SNAPSHOT"]]
  ;:source-paths ["src/clj" "src" ]
  :hoplon       {:cljsc-opts    {:pretty-print  false
                                 :optimizations :advanced}})

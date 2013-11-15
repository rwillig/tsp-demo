(defproject tsp-demo "0.1.0-SNAPSHOT"
  :description  "FIXME: add description"
  :url          "http://example.com/FIXME"
  :license      {:name  "Eclipse Public License"
                 :url   "http://www.eclipse.org/legal/epl-v10.html"}
  :plugins      [[lein-ring "0.8.7"]]
  :main tspdemo.run
  :min-lein-version "2.0.0"
  :uberjar-name "tsp-demo-standalone.jar"
  :dependencies [[org.clojure/clojure     "1.5.1"]
                 [tsp "1.1.3-SNAPSHOT"]
                 ;[incanter "1.2.3-SNAPSHOT"]
                 [ring-middleware-index-file "1.0.5-SNAPSHOT"]
                 [ring "1.2.0"]
                 [tailrecursion/hoplon "4.0.1"]]
  :source-paths ["src/clj"]
  :war-resources-path "public"
  :ring         {:handler tspdemo.run/app  })

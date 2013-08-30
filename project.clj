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
                 [tsp "1.1.0-SNAPSHOT"]
                 [net.java.dev.jets3t/jets3t "0.7.4"]
                 [tailrecursion/castra    "0.1.0-SNAPSHOT"]
                 [tailrecursion/cljson "1.0.2"]
                 [tailrecursion/hoplon "0.1.0-SNAPSHOT"]]
  :source-paths ["src/clj"]
  :hoplon       {:html-src      "src/html"
                 :cljs-src      "src/cljs"
                 :html-out      "resources/public"
                 :pretty-print  false
                 :cljsc-opts    {:warnings      true
                                 :pretty-print  false
                                 :optimizations :whitespace}})

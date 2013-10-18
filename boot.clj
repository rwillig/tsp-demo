{:project         tsp-demo 
 :version         "0.1.0-SNAPSHOT"
 :src-paths       #{"src/html" "src/clj" "src/js" "resources"}
 :dependencies    [
                  [tailrecursion/boot.task "0.1.0"]
                  [tailrecursion/hoplon "1.1.0"]
                  [tsp "1.1.2-SNAPSHOT"]
                  [ring-middleware-index-file "1.0.5-SNAPSHOT"]
                  ]
 :src-static  #{"src/static"}
 
 :require-tasks #{[tailrecursion.boot.task :as t]
                  [tailrecursion.hoplon.boot :as h]
                  }
 :tasks {:run {:doc "Start tsp demo server (port 3000)."
               :main [tspdemo.run/run-task]}}

 
 } 

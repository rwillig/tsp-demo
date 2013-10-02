{:project         tsp-demo 
 :version         "0.1.0-SNAPSHOT"
 :src-paths       #{"src/html" "src/clj"}
 :dependencies    [
                  [tailrecursion/boot.task "0.1.0-SNAPSHOT"]
                  [tailrecursion/hoplon "1.1.0-SNAPSHOT"]
                  [tsp "1.1.2-SNAPSHOT"]
                  ]
 :src-static  #{"src/static"}
 :html-out "resources/public"
 :require-tasks #{[tailrecursion.boot.task :as t]
                  [tailrecursion.hoplon.boot :as h]
                  }} 

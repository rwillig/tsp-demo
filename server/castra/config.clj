(ns castra.config)

(defonce dynamo-access  (or 
                          (System/getenv "DYNAMO_ACCESS") 
                          (System/getProperty "DYNAMO_ACCESS")))
(defonce dynamo-secret  (or 
                          (System/getenv "DYNAMO_SECRET") 
                          (System/getProperty "DYNAMO_SECRET")))

(defn init-server [ctx]
  )


(ns comments-cloud.config
  )

(def config
  (clojure.edn/read-string
    (slurp "./config.edn")))


(ns comments-cloud.spitter
  (:require 
    [comments-cloud.builder :refer [build-raw-word-data, build-word-count-data, build-simple-word-count-list, build-ready-data]]
    )
  )

;;
;;
;; Responsible for writing data out

(def spit-proper
  (fn [where, what]
    (spit 
      where
      (clojure.pprint/write
        what
        :stream nil
        :length nil
        ))))


(def spit-raw-word-data
  (fn []
    (do
      (println "@ spit-raw-word-data")
      (time
        (spit-proper
          "./generated/raw-word-data.edn"
          (clojure.pprint/write
            (build-raw-word-data)
            :stream nil
          ))))))




(def spit-word-count-data
  (fn []
    (spit-proper
      "./generated/word-count-data.edn"
      (build-word-count-data))))


(def spit-simple-word-count-list
  (fn []
    (spit-proper 
      "./generated/simple-word-count-list.edn"
      (build-simple-word-count-list))))



(def spit-ready-data
  (fn []
    (spit-proper
      "./generated/word-cloud-data.edn"
      (build-ready-data))))

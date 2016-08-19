(ns comments-cloud.color-fns.for-clj
  (:require 
    [comments-cloud.config :refer [config]]
    )
  (:import [org.apache.commons.io FilenameUtils])
  )

(def fns
  {
    "red"
      (fn [word-datum]
        (=
          "cljs"
          (FilenameUtils/getExtension 
            ((first 
              (word-datum
                :found-in))
                :where))))
    "blue"
      (fn [word-datum]
        (=
          "clj"
          (FilenameUtils/getExtension 
            ((first 
              (word-datum
                :found-in))
              :where))))
    "green"
      (fn [word-datum]
        true)
      })
      
          



          




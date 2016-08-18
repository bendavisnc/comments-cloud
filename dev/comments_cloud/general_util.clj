(ns comments-cloud.general-util
  (:require 
    [clojure.java.io :as io]
    [comments-cloud.config :refer [config]]
    )
  )

;;
;;
;; General helper methods



(def get-relative-file-path
  (fn [f]
    (clojure.string/replace
      (.getCanonicalPath f)
      (.getCanonicalPath 
        (first
          (.listFiles
            (io/file (config :parse-dir)))))
      "")))

(def spit-proper
  (fn [where, what]
    (spit 
      where
      (clojure.pprint/write
        what
        :stream nil
        :length nil
        ))))

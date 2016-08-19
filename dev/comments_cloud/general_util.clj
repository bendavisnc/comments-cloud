(ns comments-cloud.general-util
  (:require 
    [clojure.java.io :as io]
    [comments-cloud.config :refer [config]]
    )
  (:import [org.apache.commons.io FilenameUtils])
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



(def in?
  (fn [x c]
    (some #(= x %) c)))

(def parsed-found-in-list
  "Takes a list of path names and returns a cleaned up record version"
  (fn [path-list]
    (sort-by
      (fn [datum]
        (*
          (datum :times)
          -1))
      (map
        (fn [[k, v]]
          {
            :where k
            :times v
            })
        (let [
            counts (atom {})
          ]
          (do
            (doall
              (map
                (fn [list-item]
                  (if
                    (contains? @counts list-item)
                    (swap! counts assoc list-item (inc (@counts list-item)))
                    (swap! counts assoc list-item 1)))
                path-list))
          @counts))))))

(def get-all-files
  "Returns a list of all of the files given under the configured target directory that match the configured target extension"
  (fn []
    (do
      (println "@ get-all-files")
      (time
        (let [
            root-dir 
              (file-seq
                (io/file (config :parse-dir)))
            all-files
              (filter
                (fn [f]
                  (in?
                    (FilenameUtils/getExtension
                      (.getName f))
                    (config :target-ext)))
                root-dir)
          ]
          all-files)))))

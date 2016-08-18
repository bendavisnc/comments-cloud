(ns comments-cloud.builder
  (:require 
    [clojure.java.io :as io]
    [comments-cloud.config :refer [config]]
    [comments-cloud.comments-util :as comments-util]
    )
  (:import [org.apache.commons.io FilenameUtils])
  )

;;
;;
;; Responsible for actually generating our word list.


(def get-relative-file-path
  (fn [f]
    (clojure.string/replace
      (.getPath f)
      (.getCanonicalPath (io/file (config :parse-dir)))
      "")))



(def get-all-files
  "Returns a list of all of the files given under the configured target directory that match the configured target extension"
  (fn []
    (let [
        root-dir 
          (file-seq
            (io/file (config :parse-dir)))
        all-files
          (filter
            (fn [f]
              (=
                (config :target-ext)
                (FilenameUtils/getExtension
                  (.getName f))))
            root-dir)
      ]
      all-files)))

(def build-raw-word-data
  "This is where most of the heavy lifting occurs.
   Goes through all the given files and creates one big list of maps,
   each containing all of the comments found in said file, along with the file path."
  (fn []
    (map
      (fn [each-target-file]
        {
          :full-path
            (get-relative-file-path each-target-file)
          :comments-found
            (comments-util/get-all-comments-from-file each-target-file)
        })
      (get-all-files))))

(def spit-raw-word-data
  (fn []
    (spit 
      "./generated/raw-word-data.edn"
      (clojure.pprint/write
        (build-raw-word-data)
        :stream nil
        ))))


(def build-word-count
  (fn [raw-word-data]
    (do
      (let [
          counts (atom {})
        ]
        (do
          (doall
            (map
              (fn [datum]
                (do
                  (let [
                      comments (datum :comments-found)
                    ]
                    (do
                      (println comments)
                      (doall
                        (map
                          (fn [each-word]
                            (if
                              (contains?
                                @counts
                                each-word)
                                (swap! counts assoc each-word (inc (@counts each-word)))
                                (swap! counts assoc each-word 1)))
                          (mapcat
                            (fn [each-comment-in-file]
                              (clojure.string/split each-comment-in-file #" "))
                            comments)))))))
              raw-word-data))
          @counts)))))
                    

(def testt
  (fn []
    (build-word-count 
      (build-raw-word-data))))

(def test-file
  (io/file "/home/ben/Programming/forWork/comments-cloud/parse-src/java-design-patterns/dao/src/main/java/com/iluwatar/dao/CustomerDaoImpl.java"))



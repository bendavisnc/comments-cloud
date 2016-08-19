(ns comments-cloud.builder
  (:require 
    [clojure.java.io :as io]
    [comments-cloud.config :refer [config]]
    [comments-cloud.general-util :refer [in? spit-proper, get-relative-file-path]]
    [comments-cloud.comments-util :as comments-util]
    )
  (:import [org.apache.commons.io FilenameUtils])
  )

;;
;;
;; Responsible for actually generating our word list.


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


(def build-raw-word-data
  "This is where most of the heavy lifting occurs.
   Goes through all the given files and creates one big list of maps,
   each containing all of the comments found in said file, along with the file path."
  (fn []
    (do
      (println "@ build-raw-word-data")
      (time
        (map
          (fn [each-target-file]
            {
              :full-path
                (get-relative-file-path each-target-file)
              :comments-found
                (comments-util/get-all-comments-from-file each-target-file)
            })
          (get-all-files))))))

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


(def parsed-found-in-list
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




(def build-word-count-data
  (fn 
    ([]
      (build-word-count-data (build-raw-word-data)))
    ([raw-word-data]
      (do
        (println "@ build-word-count")
        (time
          (sort-by
            (fn [datum]
              (*
                (datum :count)
                -1))
            ; :count
            (filter 
              (fn [datum]
                (not
                  (in? (datum :word) (config :blacklist))))
              (pmap 
                (fn 
                  [[k, v]]
                  {
                    :word k
                    :found-in (parsed-found-in-list v)
                    :count (count v)
                  })
                (let [
                    counts (atom {})
                  ]
                  (do
                    (doall
                      (pmap
                        (fn [datum]
                          (do
                            (let [
                                comments (datum :comments-found)
                              ]
                              (doall
                                (pmap
                                  (fn [each-word]
                                    (if
                                      (contains? @counts each-word)
                                        ; (swap! counts assoc each-word (inc (@counts each-word)))
                                        ; (swap! counts assoc each-word 1)))
                                        (swap! counts assoc each-word (conj (@counts each-word) (datum :full-path)))
                                        (swap! counts assoc each-word [(datum :full-path)])))
                                  (mapcat
                                  ; (concat
                                    ; (pmap
                                      (fn [each-comment-in-file]
                                        (clojure.string/split each-comment-in-file #" "))
                                      comments))))))
                        raw-word-data))
                    @counts))))))))))


(def spit-word-count-data
  (fn []
    (spit-proper
      "./generated/word-count-data.edn"
      (build-word-count-data
        (build-raw-word-data)))))


(def build-simple-word-count-list
  (fn 
    ([]
      (build-simple-word-count-list (build-word-count-data)))
    ([word-count-data]
      (map
        :word
        word-count-data))))

(def spit-simple-word-count-list
  (fn []
    (spit-proper 
      "./generated/simple-word-count-list.edn"
      (build-simple-word-count-list))))

(def testt
  (fn []
    (build-word-count-data)))

(def test-file
  (io/file "/home/ben/Programming/forWork/comments-cloud/parse-src/java-design-patterns/dao/src/main/java/com/iluwatar/dao/CustomerDaoImpl.java"))



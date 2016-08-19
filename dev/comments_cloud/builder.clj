(ns comments-cloud.builder
  (:require 
    [clojure.java.io :as io]
    [comments-cloud.config :refer [config]]
    [comments-cloud.general-util :refer [in? get-relative-file-path, parsed-found-in-list, get-all-files]]
    [comments-cloud.comments-util :as comments-util]
    [comments-cloud.color-fns.for-clj :as for-clj]
    )
  )

;;
;;
;; Responsible for actually generating our word list.




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



(def build-simple-word-count-list
  (fn 
    ([]
      (build-simple-word-count-list (build-word-count-data)))
    ([word-count-data]
      (map
        :word
        word-count-data))))

(def build-interp-func
  (fn [maxval, minval]
    (fn [n]
      (/
        (- n minval)
        (- maxval minval)))))

(def build-color-func
  (fn []
    (let [
        fns 
          (eval 
            (read-string
              (str "comments-cloud.color-fns." (config :color-fns) "/fns")
              ))
      ]
      (fn [datum]
        (loop [
            color-fns fns
          ]
          (let [
              [color colorfn] (first color-fns) 
            ]
            (cond
              (colorfn datum)
                color
              :else
                (recur (rest color-fns)))))))))


(def build-ready-data
  (fn 
    ([]
      (build-ready-data (build-word-count-data)))
    ([word-count-data]
      (let [
          i-func (build-interp-func ((first word-count-data) :count) ((last word-count-data) :count))
          color-func (build-color-func)
        ]
        (map
          (fn [datum]
            {
              :word
                (datum :word)
              :size
                (i-func (datum :count))
              :color
                (color-func datum)
            })
          word-count-data)))))

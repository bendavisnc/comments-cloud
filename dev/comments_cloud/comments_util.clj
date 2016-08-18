(ns comments-cloud.comments-util
  (:require 
    [clojure.java.io :as io]
    [comments-cloud.config :refer [config]]
    )
  )

;;
;;
;; Any methods used for parsing out comments from files



(def non-letters-stripped
  (fn [from-string-set]
    (map
      (fn [each-string]
        (clojure.string/trim
          (apply str
            (filter
              (fn [each-letter]
                (or
                  (Character/isLetter each-letter)
                  (Character/isSpaceChar each-letter)))
              each-string))))
      from-string-set)))

(def everything-lowercase
  (fn [from-string-set]
    (map
      (fn [each-string]
        (clojure.string/lower-case each-string))
      from-string-set)))


(def get-all-comments-from-file
  "Returns a list of all of the comments (lowercased and letters only) found in a given file"
  (fn [f]
    (let [
        regexs-to-use (config :comments-regex)
        file-contents
          (slurp f)
      ]
      (everything-lowercase
        (non-letters-stripped
          (mapcat
            (fn [regex]
              (re-seq
                (re-pattern regex)
                file-contents))
            regexs-to-use))))))


; (def get-all-comments
;   (fn []
;     (let [
;         root-dir 
;           (file-seq
;             (io/file (config :parse-dir)))
;         all-java-files
;           (filter
;             (fn [f]
;               (=
;                 "java"
;                 (FilenameUtils/getExtension
;                   (.getName f))))
;             root-dir)
;       ]
;       all-java-files)))

(ns comments-cloud.playground
  (:require 
    [comments-cloud.builder :as builder]
    [comments-cloud.spitter :as spitter]
    [clojure.java.io :as io]
    )
  )

(def testt
  (fn []
    (builder/build-word-count-data)))

(def test-file
  (io/file "/home/ben/Programming/forWork/comments-cloud/parse-src/java-design-patterns/dao/src/main/java/com/iluwatar/dao/CustomerDaoImpl.java"))



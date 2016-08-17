(ns comments-cloud.core
  (:require 
    [ajax.core :refer [GET]]
    [promesa.core :as p :include-macros true]
    ))

(enable-console-print!)

; (println "This text is printed from src/comments-cloud/core.cljs. Go ahead and edit it and see reloading in action.")
;; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)


;;
;;
;; This is a simple word cloud app that targets comments found in a provided src dir
;; This heavily goes off of the work found here: http://bl.ocks.org/ericcoopey/6382449

(def desired-width 800)
(def desired-height 800)

(def create-base-svg
  (fn []
    (->
      (.select js/d3 "body")
      (.style "background-color" "black")
      (.append "svg")
      (.attr "width" desired-width)
      (.attr "height" desired-height)
      (.append "g"))))


; (create-base-svg)

(def get-word-list
  "returns a promise of a GET request that'll contain our word cloud data"
  (fn []
    (p/promise
      (fn [resolve reject]
        (GET "./generated/word-list.edn" 
          {:handler
            (fn [response]
              (resolve response))
          })))))

(def draw-everything
  (fn [words]
    (->
      (create-base-svg)
      (.selectAll "text")
      (.data words)
      (.enter)
      (.append "text")
      (.style "font-size"
        (fn [d]
          (str (.-size d) "px")))
      (.style "fill" "blue")
      (.attr "transform"
        (fn [d]
          (str "translate("(.-x d) "," (.-y d) ")")))
      (.text 
        (fn [d]
          (.-text d))))))

(def create-word-cloud
  (fn []
    (->
      (.cloud (aget js/d3 "layout"))
      (.words js/exampleData)
      (.fontSize 
        (fn [d]
          (*
            (.-size d))))
      (.size (clj->js [desired-width desired-height]))
      (.spiral "rectangular") 
      (.on "end" draw-everything)
      (.start))))


(def testt
  (fn []
    (do
      (create-word-cloud))))



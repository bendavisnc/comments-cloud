  (ns comments-cloud.core
  (:require 
    [ajax.core :refer [GET]]
    [promesa.core :as p :include-macros true]
    ))

(enable-console-print!)

(println "This text is printed from src/comments-cloud/core.cljs. Go ahead and edit it and see reloading in action.")
; define your app data so that it doesn't get over-written on reload

(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
)


;
;
; This is a simple word cloud app that targets comments found in a provided src dir
; This heavily goes off of the work found here: http://bl.ocks.org/ericcoopey/6382449

(def desired-width 1600)
(def desired-height 1200)
; (def desired-height 1600)
; (def desired-width 500)
; (def desired-height 500)

(def create-base-svg
  (fn []
    (let
      [
        selection
          (->
            (.select js/d3 "body")
            ; (.style "background-color" "black")
            (.append "svg")
            (.attr "width" desired-width)
            (.attr "height" desired-height)
            (.append "g")
            ; .attr("transform", "translate(" + layout.size()[0] / 2 + "," + layout.size()[1] / 2 + ")")
            (.attr "transform"
              (str "translate(" (/ desired-width 2)"," (/ desired-height 2) ")")))
      ]
      (do
        (->
          selection
          (.append "rect")
          (.attr "width" desired-width)
          (.attr "height" desired-height)
          (.attr "fill" "black")
          (.attr "transform"
            (str "translate(" (/ desired-width -2)"," (/ desired-height -2) ")")))
        selection))))
        ; )
      ; (->
        ; (.select js/d3 "svg")
        ; (.select "g")))))
      


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
      (.style "font-family", "Impact")
      (.attr "text-anchor", "middle")
      (.attr "transform"
        (fn [d]
          (str 
            "translate("(.-x d) "," (.-y d) ")"
            "rotate("(.-rotate d)")")))
      (.text 
        (fn [d]
          (.-text d))))))

(def create-word-cloud
  (fn []
    (->
      (.cloud (aget js/d3 "layout"))
      (.words js/exampleData)
      (.font "Impact")
      (.fontSize 
        (fn [d]
          ; (.-size d)))
          (*
            (.-size d) 10)))
      (.size (clj->js [desired-width desired-height]))
      ; (.padding 4)
      ; (.rotate
        ; (fn []
          ; (* 2 (rand-int 2) 90)))
          ; (* (rand) 360)))
          ; (rand-nth (range -30 30 10))))
      (.on "end" draw-everything)
      (.start))))


(def testt
  (fn []
    (do
      (create-word-cloud))))



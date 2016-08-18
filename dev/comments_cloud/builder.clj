(ns comments-cloud.builder
  )

;;
;;
;; Responsible for actually generating our word list.

(def config
	(clojure.edn/read-string
		(slurp "./config.edn")))

(def build-word-list
	(fn []
		[
		  "bananas"
		  "cranberries"
		  "rasberries"]))


(def spit-word-list
	(fn []
		(spit
			"./generated/word-list.edn"
			(build-word-list))))

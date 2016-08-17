(ns comments-cloud.builder
  )

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

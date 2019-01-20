(ns wordcloud4ch.core
  (:gen-class)
  (:require [clojure.data.json :as json]))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))


(json/read-str (slurp "https://a.4cdn.org/a/catalog.json")
               :key-fn keyword)

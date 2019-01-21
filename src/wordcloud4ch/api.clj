(ns wordcloud4ch.api
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.string :as str]))

(def url "https://a.4cdn.org/")

(defn req-link [option {board :board number :number}]
  (let [prefix #(str/join "/" [board %])]
    (case option
      :boards     "boards.json"
      :catalog    (prefix "catalog.json")
      :archive    (prefix "archive.json")
      :threads    (prefix "threads.json")
      :thread     (prefix (str "thread/" number ".json"))
      :boardpage  (prefix (str number ".json")))))

(defn req
  ([option]
   (req option {}))
  ([option args]
   (json/read-str
    (slurp (str url (req-link option args)))
    :key-fn keyword)))

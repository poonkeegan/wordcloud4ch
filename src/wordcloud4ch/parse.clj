(ns wordcloud4ch.parse
  (:gen-class)
  (:require [clojure.data.json :as json]))

(defn ws-filter [worksafe threads]
  (if worksafe
    (filter #(= 1 (:ws_board %)) threads)
    threads))

(defn boards [boardsjson worksafe]
  (->> boardsjson
       (:boards)
       (ws-filter worksafe)
       (map :board)))

(defn threadnums [threadsjson]
  (->> threadsjson
       (map :threads)
       (flatten)
       (map :no)))

(defn comment-text [post-json]
  (->> post-json
       (map :com)))

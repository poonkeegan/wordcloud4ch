(ns wordcloud4ch.core
  (:gen-class)
  (:require [clojure.data.json :as json]
            [clojure.java.io :as io]
            [clojure.string :as str]
            [wordcloud4ch.api :as api]
            [wordcloud4ch.parse :as parse])
  (:import (java.awt Dimension Color)
           (net.htmlparser.jericho Source TextExtractor)
           (com.kennycason.kumo WordCloud WordFrequency CollisionMode)
           (com.kennycason.kumo.bg CircleBackground)
           (com.kennycason.kumo.palette ColorPalette)
           (com.kennycason.kumo.font KumoFont FontWeight)
           (com.kennycason.kumo.font.scale SqrtFontScalar)))

(def board "a")

(def stopwords
  (-> "stopwords.txt"
      (io/resource)
      (io/file)
      (slurp)
      (#(str/split % #"\s+"))
      (set)))

(def boards
  (-> :boards
      (api/req)
      (parse/boards true)))

(defn threads [board]
  (-> :threads
      (api/req {:board board})
      (parse/threadnums)))

(defn comments [threads]
  (->> threads
       (map #(api/req :thread {:board board :number %}))
       (map :posts)
       (flatten)
       (map :com)))

(defn strip-html [string]
  (-> string
      (Source.)
      (TextExtractor.)
      (.toString)))

(defn words [comments]
  (->> comments
       (map strip-html)
       (map #(str/split % #"\s+"))
       (flatten)
       (map str/lower-case)
       (filter not-empty)
       (filter #(not (contains? stopwords %)))))

(defn wordpredicate [[k v]]
  (> v 20))

(defn freq [words]
  (frequencies words))

(defn strip-punc [string]
  (str/replace string #"\A\W*(.*\w)(\W)*\z" "$1"))

(def a (->> "a"
            (threads)
            (comments)
            (words)
            (filter #(not (re-matches #"\W*" %)))
            (map strip-punc)
            (freq)
            (filter wordpredicate)
            (sort-by second)))

(def dims (Dimension. 500 312))

(defn -main
  [& args]
  (let [wordFreq (map (fn [[hd tl]] (WordFrequency. hd tl)) a)
        dims (Dimension. 500 312)
        wc (WordCloud. dims CollisionMode/PIXEL_PERFECT)]
    (doto wc
      (.setPadding 2)
      (.setBackground (CircleBackground. 300))
      (.setColorPalette (ColorPalette. [(Color. 0x4055F1)
                                        (Color. 0x408DF1)
                                        (Color. 0x40AAF1)
                                        (Color. 0x40C5F1)
                                        (Color. 0x40D3F1)
                                        (Color. 0xFFFFFF)]))
      (.setFontScalar (SqrtFontScalar. 10 40))
      (.build wordFreq)
      (.writeToFile "test.png"))))

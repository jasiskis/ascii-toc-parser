(ns allstreet-challenge.logic
  (:require [clojure.string :as string]))

(defn parse-line [line]
  (let [[level & text] (string/split line #" ")]
    {:level (count level)
     :text (string/join " " text)}))

(ns allstreet-challenge.logic
  (:require [clojure.string :as string]))

(def last-element-in-level "└──")
(def nested-element "├──")
(def spacer "    ")
(def open-parent "│")

(defn parse-line [line]
  (let [[level & text] (string/split line #" ")]
    {:level (count level)
     :text (string/join " " text)}))

(defn add-prefix [node]
  (if (:last-in-level node)
    (assoc node :prefix last-element-in-level)
    (assoc node :prefix nested-element)))

(defn add-spacer [node]
  (->> (reduce (fn [acc open]
                (if open
                  (str acc open-parent spacer)
                  (str acc spacer))) "" (:open-parents node))
    (assoc node :spacer)))

(defn build-tree [nodes]
  (let [nodes' (reverse nodes)]
    (loop [])))


(defn enrich-node [node next-node]
  (let [current-level (:level node)
        next-node-level (:level next-node)]
    (cond
      (not next-node) (assoc node :last-in-level true
                             :open-parents (take (dec current-level)
                                                       (repeat false)))
      (= current-level
         next-node-level) (assoc node :last-in-level false 
                                 :open-parents (:open-parents next-node))
      (> current-level
         next-node-level) (assoc node 
                                 :last-in-level true
                                 :open-parents 
                                 (conj (:open-parents next-node) true)))))

#_ (loop [[a & rest] [5 4 4 3 2 1]
       previous-node {}
       tree []]
  (if-not previous-node
    (recur ))
  (cond
    ())
  (if-not rest
    tree
    (recur rest)))

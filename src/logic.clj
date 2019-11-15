(ns allstreet-challenge.logic
  (:require [clojure.string :as string]))

(def last-element-in-level "└── ")
(def nested-element "├── ")
(def spacer "    ")
(def open-parent "│   ")
(def root {:level 0
           :text "."
           :prefix ""
           :spacer ""})

(defn parse-line [line]
  (let [[level & text] (string/split line #" ")]
    {:level (count level)
     :text (string/join " " text)}))

(defn add-prefix [heading]
  (if (:last-in-level heading)
    (assoc heading :prefix last-element-in-level)
    (assoc heading :prefix nested-element)))

(defn add-spacer [heading]
  (->> (reduce (fn [acc open]
                 (if open
                   (str acc open-parent)
                   (str acc spacer))) "" (:open-parents heading))
       (assoc heading :spacer)))

(defn false-collection
  ([n]
   (vec (take n (repeat false))))
  ([n coll]
   (vec (take n (concat coll (repeat false))))))

(defn last-heading-in-table [heading]
  (assoc heading :last-in-level true
         :open-parents (false-collection (dec (:level heading)))))

(defn heading-in-the-same-level [heading next-heading]
  "a node succeeded by a same level is not the last-in-level
   and has same open parents
   e.g: 
    ### Heading 3
    ### Heading 3"
  (assoc heading
         :last-in-level false
         :open-parents (:open-parents next-heading)))

(defn stepping-out-level [heading next-heading]
  "Stepping out means that it is last-in-level if there's 
   no next same-level heading
   and has the same open-parents as the child heading
    e.g: 
     ### Heading 3
     #### Heading 4"
  (let [current-level (:level heading)
        relevant-parents (take current-level (:open-parents next-heading))]
    (assoc heading
           :last-in-level (not (last relevant-parents))
           :open-parents (or (-> relevant-parents butlast vec)
                             []))))

(defn stepping-in-level [heading next-heading]
  "Stepping in means that the heading is the last in that level
   and has open levels before it
    e.g: 
     #### Heading 4
     ### Heading 3"
  (assoc heading
         :last-in-level true
         :open-parents
         (false-collection (dec (:level heading))
                           (conj (:open-parents next-heading) true))))

(defn enrich-heading [heading next-heading]
  "enriches the current heading based on the info provided by the next-heading"
  (let [current-level (:level heading)
        next-heading-level (:level next-heading)]
    (cond
      (not next-heading) (last-heading-in-table heading)
      (= current-level
         next-heading-level) (heading-in-the-same-level heading next-heading)
      (< current-level
         next-heading-level) (stepping-out-level heading next-heading)
      (> current-level
         next-heading-level) (stepping-in-level heading next-heading))))

(defn enrich-headings [headings]
  "traverses the collection in reverse accumulating enriched-headings"
  (reverse (reduce (fn [acc heading]
                     (conj acc (enrich-heading heading (last acc)))) [] (reverse headings))))

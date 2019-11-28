(ns ascii-toc-parser.table-of-contents
  (:require [clojure.string :as string]
            [ascii-toc-parser.logic :as logic]))

(defn- render [heading]
  (str (:spacer heading) (:prefix heading) (:text heading)))

(defn- add-root [coll]
  (conj coll logic/root))

(defn table-of-contents [document-str]
  (->> (map logic/parse-line (string/split-lines document-str))
       (logic/enrich-headings)
       (map logic/add-spacer)
       (map logic/add-prefix)
       add-root
       (map render)
       (string/join \newline)))

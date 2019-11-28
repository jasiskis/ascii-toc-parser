(ns ascii-toc-parser.main
  (:require [ascii-toc-parser.table-of-contents :as table-of-contents]))


(defn -main []
  (let [input-string (slurp *in*)]
    (println (table-of-contents/table-of-contents input-string))))

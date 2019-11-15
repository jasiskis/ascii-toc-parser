(ns allstreet-challenge.main
  (:require [allstreet-challenge.table-of-contents :as table-of-contents]))


(defn -main []
  (let [input-string (slurp *in*)]
    (println (table-of-contents/table-of-contents input-string))))

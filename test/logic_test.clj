(ns allstreet-challenge.logic-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]
            [allstreet-challenge.logic :as logic]))

(deftest parse-line-test
  (testing "one level heading"
    (is (match? {:level 1
                 :text "heading test test"}
                (logic/parse-line "# heading test test"))))

  (testing "two level heading"
    (is (match? {:level 2
                 :text "heading test test"}
                (logic/parse-line "## heading test test"))))

  (testing "N level heading"
    (is (match? {:level 15 
                 :text "heading test test"}
                (logic/parse-line (str (apply str (take 15 (repeat "#"))) " heading test test"))))))

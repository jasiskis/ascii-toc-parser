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

(deftest add-prefix-test
  (testing "for single level add └──"
    (is (match? {:prefix logic/last-element-in-level}
                (logic/add-prefix {:level 1
                                   :text "heading test test"
                                   :last-in-level true}))))

  (testing "for nested level should add ├──"
    (is (match? {:prefix logic/nested-element}
                (logic/add-prefix {:level 2
                                   :text "heading 2"})))))

(deftest add-spacer-test 
  (testing "should not add space for level 1"
    (is (match? {:spacer ""}
                (logic/add-spacer {:level 1
                                   :text "abc"
                                   :open-parents []}))))

  (testing "should add a spacer for level 2 wo open parents"
    (is (match? {:spacer logic/spacer}
                (logic/add-spacer {:level 2
                                   :text "abc"
                                   :open-parents [false]}))))

  (testing "should add a | and a spacer for level 2 with open parent"
    (is (match? {:spacer logic/open-parent}
                (logic/add-spacer {:level 2
                                   :text "abc"
                                   :open-parents [true]}))))

  (testing "should add a |  spacer  |  spacer | spacer for level 3 with 3 open parent"
    (is (match? {:spacer (str logic/open-parent logic/open-parent logic/open-parent)}
                (logic/add-spacer {:open-parents [true true true]}))))

  (testing "should add a |  spacer  spacer | spacer for level 3 with 2 open parent"
    (is (match? {:spacer (str logic/open-parent logic/spacer logic/open-parent)}
                (logic/add-spacer {:open-parents [true false true]})))))

(deftest enrich-heading
  (testing "single-heading"
    (is (match? {:level 1
                 :open-parents []
                 :last-in-level true}
                (logic/enrich-heading {:level 1} nil))))
  (testing "two headings same level"
    (is (match? {:level 1
                 :open-parents []
                 :last-in-level false}
                (logic/enrich-heading {:level 1} {:level 1
                                               :last-in-level true
                                               :open-parents []}))))
  (testing "last-heading should have level - 1 closed parents"
    (is (match? {:level 5
                 :open-parents [false false false false]
                 :last-in-level true}
                (logic/enrich-heading {:level 5} nil))))
  (testing "add open parent for current level greater than next"
    (is (match? {:level 5
                 :open-parents [true false true true]
                 :last-in-level true}
                (logic/enrich-heading {:level 5} {:level 4
                                               :last-in-level true
                                               :open-parents [true false true]}))))
  (testing "fill gap for current level greater than next"
    (is (match? {:level 5
                 :open-parents [true false true false]
                 :last-in-level true}
                (logic/enrich-heading {:level 5} {:level 3
                                               :last-in-level false 
                                               :open-parents [true false]}))))
  (testing "current level less than next-level with open parent"
    (is (match? {:level 3
                 :open-parents [true true]
                 :last-in-level false}
                (logic/enrich-heading {:level 3} {:level 4
                                               :last-in-level false 
                                               :open-parents [true true true]}))))
  (testing "current level less than next-level wo open parent"
    (is (match? {:level 3
                 :open-parents [true true]
                 :last-in-level true}
                (logic/enrich-heading {:level 3} {:level 4
                                               :last-in-level false 
                                               :open-parents [true true false]})))))

; # heading 1
; ## heading 2
; ## another heading 2
; ### heading 3
; #### heading x
; ## heading final
(def full-example 
  [{:level 1}
   {:level 2}
   {:level 2}
   {:level 3}
   {:level 4}
   {:level 2}])

(deftest enrich-headings-test
  (testing "nested elements"
    (is (match? [{:level 1, :last-in-level true, :open-parents []}
                 {:level 2, :last-in-level false, :open-parents [false]}
                 {:level 2, :last-in-level false, :open-parents [false]}
                 {:level 3, :last-in-level true, :open-parents [false true]}
                 {:level 4, :last-in-level true, :open-parents [false true false]}
                 {:level 2, :last-in-level true, :open-parents [false]}]
                 (logic/enrich-headings full-example)))))

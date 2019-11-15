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
    (is (match? {:prefix "└──"}
                (logic/add-prefix {:level 1
                                   :text "heading test test"
                                   :last-in-level true}))))

  (testing "for nested level should add ├──"
    (is (match? {:prefix "├──"}
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
    (is (match? {:spacer (str logic/open-parent logic/spacer)}
                (logic/add-spacer {:level 2
                                   :text "abc"
                                   :open-parents [true]}))))

  (testing "should add a |  spacer  |  spacer | spacer for level 3 with 3 open parent"
    (is (match? {:spacer (str logic/open-parent logic/spacer logic/open-parent logic/spacer logic/open-parent logic/spacer)}
                (logic/add-spacer {:open-parents [true true true]}))))

  (testing "should add a |  spacer  spacer | spacer for level 3 with 2 open parent"
    (is (match? {:spacer (str logic/open-parent logic/spacer logic/spacer logic/open-parent logic/spacer)}
                (logic/add-spacer {:open-parents [true false true]})))))


#_(deftest build-tree
  (testing "single level tree"
    (is (match? {:level 0
                 :children 
                 [{:level 1
                  :text "abc"
                  :children []}]}
                (logic/build-tree [{:level 1
                                    :text "abc"}]))))
  (testing "two level tree"
    (is (match? {:level 0
                 :children
                 [{:level 1
                   :text "abc"
                   :children 
                   [{:level 2
                     :text "abc"
                     :open-parents [false]}]}]}
                (logic/build-tree [{:level 1
                                    :text "abc"}
                                   {:level 2
                                    :text "abc"}])))))
(deftest enrich-node
  (testing "single-node"
    (is (match? {:level 1
                 :open-parents []
                 :last-in-level true}
                (logic/enrich-node {:level 1} nil))))
  (testing "two nodes same level"
    (is (match? {:level 1
                 :open-parents []
                 :last-in-level false}
                (logic/enrich-node {:level 1} {:level 1
                                               :last-in-level true
                                               :open-parents []}))))
  (testing "last-node should have level - 1 closed parents"
    (is (match? {:level 5
                 :open-parents [false false false false]
                 :last-in-level true}
                (logic/enrich-node {:level 5} nil))))
  (testing "add open parent for current level greater than next"
    (is (match? {:level 5
                 :open-parents [true false true true]
                 :last-in-level true}
                (logic/enrich-node {:level 5} {:level 4
                                               :last-in-level true
                                               :open-parents [true false true]})))))

(ns ascii-toc-parser.logic-test
  (:require [clojure.test :refer :all]
            [matcher-combinators.test]
            [matcher-combinators.matchers :as m]
            [ascii-toc-parser.table-of-contents :as table-of-contents]))

(def example
  "# Heading 1
## Heading 2
## Another Heading 2
### Heading 3
#### Heading X
## Heading Final ")

(def expected
  ".
└── Heading 1
    ├── Heading 2
    ├── Another Heading 2
    │   └── Heading 3
    │       └── Heading X
    └── Heading Final")

(def complex-table
  "# Heading 1
#### Heading 4
### Heading 3
#### Heading 4
#### Heading 4
## heading 2
# Heading")

(def complex-expected
  ".
├── Heading 1
│   │   │   └── Heading 4
│   │   └── Heading 3
│   │       ├── Heading 4
│   │       └── Heading 4
│   └── heading 2
└── Heading")

(deftest table-of-contents-test
  (testing "example table"
    (is (match? expected
                (table-of-contents/table-of-contents example))))
  (testing "complex table"
    (is (match? complex-expected
                (table-of-contents/table-of-contents complex-table)))))

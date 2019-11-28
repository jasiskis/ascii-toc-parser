# Table of Contents to ASCII Tree parser

Given a MD table of contents it prints a ASCII tree (like the `tree` command).

## Running the program

```
cat example |  clj -m ascii-toc-parser.main
```
it will ouptut:
```
.
└── Heading 1
    ├── Heading 2
    ├── Another Heading 2
    │   └── Heading 3
    │       └── Heading X
    └── Heading Final
```

## Running tests

`clj -A:test`

## Design
My approach to this problem was to:

- First parse the input to a data-structure
- Traversing the collection backwards to enrich the data-structure with relevant information like if there are open-parent to keep track of the `│` and if the heading is final to use either `└` or `├`.
- After the data-structure is enriched with the relevant information is easy to add the immediate prefix and the level spacer.

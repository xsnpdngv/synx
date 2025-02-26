[![Clojure CI](https://github.com/xsnpdngv/synx/actions/workflows/clojure.yml/badge.svg)](https://github.com/xsnpdngv/synx/actions/workflows/clojure.yml)

# EBNF/ABNF Syntax Checker

Define grammar with EBNF/ABNF and check input file(s) against it from the command line


## Build

```bash
npm install
npm run build
```

## Run

```bash
node synx.cjs
# => Usage: node synx.cjs [--abnf] <grammar.xbnf> <input1.txt> [<input2.txt> ...]


node synx.cjs test/grammar.ebnf test/input*.txt
# => [ CHECK    ]  test/input-nok.txt
#    Parse error at line 2, column 3:
#    in-correctFnName2
#      ^
#    Expected one of:
#    #"[ \t\n]"
#    "_"
#    #"[0-9]"
#    #"[A-Za-z]"
# 
#    [    ERROR ]  test/input-nok.txt
#    [ CHECK    ]  test/input-ok.txt
#    [       OK ]  test/input-ok.txt


node synx.cjs --abnf test/grammar.abnf test/input*.txt
# => [ CHECK    ]  test/input-nok.txt
#    Parse error at line 2, column 3:
#    in-correctFnName2
#      ^
#    Expected one of:
#    %x000a
#    %x0009
#    " "
#    "_"
#    #"[0-9]"
#    #"[a-zA-Z]"
#
#    [    ERROR ]  test/input-nok.txt
#    [ CHECK    ]  test/input-ok.txt
#    [       OK ]  test/input-ok.txt
```

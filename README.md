[![Clojure CI](https://github.com/xsnpdngv/synx/actions/workflows/clojure.yml/badge.svg)](https://github.com/xsnpdngv/synx/actions/workflows/clojure.yml)

# Command-line EBNF/ABNF Syntax Checker

Define grammar with EBNF/ABNF and check input file(s) or standard input against it from the command line


## Build

```bash
npm install
npm run build
```


## Run

```bash
node synx.cjs
# => Usage: node synx.cjs [--abnf] <grammar-(file|url)> <input-file1> [...] [:start-rule]
#           echo 'text' | node synx.cjs [--abnf] <grammar-(file|url)> [:start-rule]


node synx.cjs --abnf http://sip.asdf.hu sip-invite.txt
# => [ CHECK    ]  sip-invite.txt
#    [       OK ]  sip-invite.txt


echo -n "Contact: sip:alice@atlanta.com" | node synx.cjs --abnf http://sip.asdf.hu  :Contact
echo $?
# => 0


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


echo "%@#$&^*#" | node synx.cjs test/grammar.ebnf
# => Parse error at line 1, column 1:
#    %@#$&^*#
#    ^
#    Expected one of:
#    #"[A-Za-z]"
#    #"[ \t\n]"
```

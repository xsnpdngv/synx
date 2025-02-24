# ClojureScript Syntax checker using Instaparse

Define grammar with EBNF and check an input file against it

```bash
npm install
npm run build
node synx.cjs grammar.ebnf input.txt input-bad.txt
```

```log
[ CHECK    ]  input.txt
[       OK ]  input.txt
[ CHECK    ]  input-bad.txt
Parse error at line 2, column 3:
in-correctFnName2
  ^
Expected one of:
#"[ \t\n]"
"_"
#"[0-9]"
#"[A-Za-z]"
[    ERROR ]  input-bad.txt
```

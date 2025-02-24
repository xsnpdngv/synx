(ns synx
  (:require [instaparse.core :as insta]
            ["fs" :as fs]))

(defn read-file [filename]
  (try
    (fs/readFileSync filename "utf-8")
    (catch js/Error e
      (println "Error reading file:" filename)
      (js/process.exit 1))))

(defn validate-input [grammar-file input-file]
  (let [grammar (read-file grammar-file)
        input (read-file input-file)
        parser (insta/parser grammar)
        result (parser input)]
    (if (insta/failure? result)
      (do (println (insta/get-failure result))
          false)  ;; Return false instead of exiting
      true)))  ;; Return true if valid input

;; Command-line execution
(defn -main [& args]
  (if (not= 2 (count args))
    (do (println "Usage: node synx.cls <grammar.ebnf> <input.txt>")
        (js/process.exit 1))
    (let [is-valid (apply validate-input args)]
      (if is-valid
        (println "Valid input")
        (do (js/process.exit 1))))))

(set! *main-cli-fn* -main)

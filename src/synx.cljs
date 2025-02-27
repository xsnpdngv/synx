(ns synx
  (:require [instaparse.core :as insta]
            ["fs" :as fs]))

(defn read-file [filename]
  (try
    (fs/readFileSync filename "utf-8")
    (catch js/Error e
      (println "Error reading file:" filename)
      (js/process.exit 1))))

(defn create-parser [grammar-file abnf?]
  (let [grammar (read-file grammar-file)]
    (if abnf?
      (insta/parser grammar :input-format :abnf)
      (insta/parser grammar))))

(defn validate-input [parser input-file]
  (println "[ CHECK    ] " input-file)
  (let [input (read-file input-file)
        result (parser input)]
    (if (insta/failure? result)
      (do (println (insta/get-failure result))
          (println "[    ERROR ] " input-file)
          false)
      (do (println "[       OK ] " input-file)
          true))))

(defn -main [& args]
  (if (< (count args) 2)
    (do (.write js/process.stderr "Usage: node synx.cjs [--abnf] <grammar.xbnf> <input1.txt> [<input2.txt> ...]\n")
        (js/process.exit 1))
    (let [abnf? (some #(= % "--abnf") args)
          files (remove #(= % "--abnf") args)
          grammar-file (first files)
          parser (create-parser grammar-file abnf?)
          input-files (rest files)
          results (doall (map #(validate-input parser %) input-files))
          all-valid? (every? true? results)]  ;; Check if all files passed
      (js/process.exit (if all-valid? 0 1)))))

(set! *main-cli-fn* -main)

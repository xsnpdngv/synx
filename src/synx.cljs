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

(defn -main [& args]
  (if (< (count args) 2)
    (do (.write js/process.stderr "Usage: node synx.cjs <grammar.ebnf> <input1.txt> [<input2.txt> ...]\n")
        (js/process.exit 1))
    (let [grammar-file (first args)
          input-files (rest args)
          results (doall (map (fn [input-file]
                                (println "[ CHECK    ] " input-file)
                                (let [valid? (validate-input grammar-file input-file)]
                                  (if valid?
                                    (println "[       OK ] " input-file)
                                    (println "[    ERROR ] " input-file))
                                  valid?)) 
                              input-files))
          all-valid? (every? true? results)]  ;; Check if all files passed
      (if (not all-valid?) 
        (js/process.exit 1)  ;; Exit with error if any file is invalid
        (js/process.exit 0))))) ;; Exit with success if all files are valid

(set! *main-cli-fn* -main)

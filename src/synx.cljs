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
  (let [input (read-file input-file)
        result (parser input)]
    (if (insta/failure? result)
      (do (println (insta/get-failure result))
          false)
      true)))

(defn -main [& args]
  (if (< (count args) 2)
    (do (.write js/process.stderr "Usage: node synx.cjs [--abnf] <grammar.xbnf> <input1.txt> [<input2.txt> ...]\n")
        (js/process.exit 1))
    (let [[options files] (split-with #(= % "--abnf") args)
          abnf? (some #(= % "--abnf") options)
          grammar-file (first files)
          parser (create-parser grammar-file abnf?)
          input-files (rest files)
          results (doall (map (fn [input-file]
                                (println "[ CHECK    ] " input-file)
                                (let [valid? (validate-input parser input-file)]
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

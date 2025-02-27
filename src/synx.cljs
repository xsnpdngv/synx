(ns synx
  (:require [instaparse.core :as insta]
            ["fs" :as fs]))


(defn read-file [filename]
  (try
    (fs/readFileSync filename "utf-8")
    (catch js/Error e
      (println "Error reading file:" filename)
      (js/process.exit 1))))


(defn read-stdin []
  (let [stdin (.-stdin js/process)]
    (set! (.-encoding stdin) "utf8")
    (let [data (atom "")]
      (.on stdin "data" #(swap! data str %))
      (js/Promise.
        (fn [resolve _reject]
          (.on stdin "end" #(resolve @data)))))))


(defn create-parser [grammar-file abnf?]
  (let [grammar (read-file grammar-file)]
    (apply insta/parser grammar (when abnf? [:input-format :abnf]))))


(defn validate-input [parser input]
  (let [result (parser input)]
       (if (insta/failure? result)
          (do (println (insta/get-failure result))
              false)
          true)))


(defn validate-input-file [parser input-file]
  (println "[ CHECK    ] " input-file)
  (if (validate-input parser (read-file input-file))
    (do (println "[    ERROR ] " input-file)
        false)
    (do (println "[       OK ] " input-file)
        true)))


(defn -main [& args]
  (if (< (count args) 1)
    (do (.write js/process.stderr "Usage: node synx.cjs [--abnf] <grammar-file> <input-file1> [<input-file2> ...]\n")
        (.write js/process.stderr "       echo 'text' | node synx.cjs [--abnf] <grammar-file>\n\n")
        (js/process.exit 1)))
  (let [abnf? (some #(= % "--abnf") args)
        files (remove #(= % "--abnf") args)
        grammar-file (first files)
        input-files (rest files)
        parser (create-parser grammar-file abnf?)]
       (if (empty? input-files)
         (-> (read-stdin)
             (.then (fn [stdin-input] (js/process.exit (validate-input parser stdin-input)))))
         (let [results (doall (map #(validate-input-file parser %) input-files))
              all-valid? (every? true? results)]
              (js/process.exit (if all-valid? 0 1))))))


(set! *main-cli-fn* -main)

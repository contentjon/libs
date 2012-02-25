(ns libs.templates
  (:use (libs maps parse regex)))

(defn make-template [& parts]
  (vec parts))

(defn pad-string [s n]
  (apply str s (repeat (- n (count s)) " ")))

(defn str-to-length [s n]
  (.substring (pad-string s n) 0 n))

(defn template->string [templ cont]
  "Will create a string from a template, given a map with the substitutions"
  (apply
   str
   (interpose " "
              (map
               (fn [s]
                 (cond
                  (vector? s) ((first s) cont)
                  (keyword? s) (s cont)
                  :else s))
               templ))))

(defn parse-from-template [templ s]
  (when-not (or (nil? templ) (nil? s))
    (let [splice (fn splice [xs]
                   (apply concat
                          (map #(if (seq? %)
                                  (splice %)
                                  [%])
                               xs)))
          spliced-templ (splice templ)
          compiled-templ (map #(cond
                                (vector? %) (named (first %) (second %))
                                (keyword? %) (named % word)
                                :else %)
                              spliced-templ)]
      (when-let
          [result (match-re (->> compiled-templ
                                 (interpose (many ws))
                                 regex)
                            s)]
        (let [postprocessors (->> spliced-templ
                                  (map (fn [x]
                                         (when (vector? x)
                                           (let [[key _ postprocessor] x]
                                             (when postprocessor
                                               [key postprocessor])))))
                                  (remove nil?))]
          (if (string? result)
            {:matched result}
            (-> result
                (dissoc nil)
                (update-with postprocessors))))))))

(def not-nil? (complement nil?))

(defn parse-from-templates [templates block]
  (let [results (map parse-from-template templates block)]
    (when (every? not-nil? results)
      (apply merge results))))

(defn to-parser
  ([template]
     (fn [block]
       (when-let [res (parse-from-template template (first block))]
         [res (next block)])))
  ([template & more]
     (fn [block]
       (when-let [res (parse-from-templates (list* template more) block)]
         [res (drop (inc (count more)) block)]))))

(defn template-parser [templ]
  (fn [s]
    (parse-from-template templ s)))

(defn as-int [key]
  [key integer parse-int])

(defn as-float [key]
  [key floating parse-float])

(defn in-parens [& args]
  (concat ["("] args [")"]))

(defn in-brackets [& args]
  (concat ["["] args ["]"]))

(defn in-braces [& args]
  (concat ["{"] args ["}"]))

(ns libs.templates
  (:use (libs maps regex)))

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
  (when-not (nil? templ)
    (let [extended-templ (interpose (many ws) templ)]
      (when-let
          [result (match-re
                   (regex
                    (map #(cond
                           (vector? %) (named (first %) (second %))
                           (keyword? %) (named % (at-least-one non-ws))
                           :else %)
                         extended-templ))
                   s)]
        (let [postprocessors (->> templ
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

(def parse-int #(Integer. %))

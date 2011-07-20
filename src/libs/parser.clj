(ns libs.parser
  (:use (libs fn)))

(defn fail []
  ::fail)

(defn- parser-helper [stream-sym bindings body]
  (let [[lhs rhs & more-bindings] bindings]
    (if lhs
      `(when-let [parse-result# (~rhs ~stream-sym)]
         (let [[~lhs ~stream-sym] parse-result#]
           ~(parser-helper stream-sym more-bindings body)))
      `(let [res# (do ~@body)]
         (if (= ::fail res#)
           nil
           [res# ~stream-sym])))))

(defmacro parser [bindings & body]
  (let [stream-sym (gensym)]
    `(fn [~stream-sym]
       ~(parser-helper stream-sym bindings body))))

(defn return [x]
  (fn [stream]
    [x stream]))

(def eos
  (fn [stream]
    (when (empty? stream)
      [nil nil])))

(def any
  (fn [stream]
    (when-not (empty? stream)
      [(first stream) (next stream)])))

(defn satisfies [pred]
  (parser [res any]
    (if (pred res)
      res
      (fail))))

(defn eq [x]
  (satisfies #(= % x)))

(defn choice
  [options]
  (satisfies (into #{} options)))

(defn times [p n]
  (fn [stream]
    (if (zero? n)
      [nil stream]
      ((parser [fst p
                rst (times p (dec n))]
         (cons fst rst))
       stream))))

(defn pnot [p]
  (fn [stream]
    (if (p stream)
      nil
      [nil stream])))

(def por or-fn)

(def pand and-fn)

(defn surround [p before after]
  (parser [_ before
           res p
           _ after]
    res))

(declare at-least-one)

(defn many [p]
  (por (at-least-one p)
       (return nil)))

(defn at-least-one [p]
  (parser [fst p
           rst (many p)]
    (cons fst rst)))

(def anything (many any))
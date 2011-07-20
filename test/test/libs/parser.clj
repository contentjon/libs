(ns test.libs.parser
  (:refer-clojure :as c)
  (:require [midje.sweet :as m])
  (:use [midje.sweet :exclude [anything]]
        [libs.parser :reload :true]))

(fact ((parser [] :foo) :any) => [:foo :any]
      ((parser [] (fail)) :any) => nil
      ((parser [x any] x) "hoho") => [\h (seq "oho")])

(let [one (parser [r any
                   _ eos]
            r)]
  (fact (one "f")  => [\f m/anything]
        (one "fo") => nil))

(fact ((eq \h) "hoho") => [\h (seq "oho")]
      ((eq \h) "goho") => nil)

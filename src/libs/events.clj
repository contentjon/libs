(ns libs.events
  (:require [libs.java.meta :as m])
  (:use (libs log)))

(defmulti handle (fn [receiver key & args]
                   [(m/type receiver) key]))

(defmacro defhandler [receiver key args & rest]
  `(defmethod handle [~receiver ~key] [_# _# ~@args] ~@rest))

(defmulti handle-default (fn [receiver & _] receiver))

(defmacro def-default-handler [receiver args & rest]
  `(defmethod handle-default ~receiver [_# ~@args] ~@rest))

(defmethod handle-default :default
  [receiver key & _]
  (debug "unhandled event" (m/type receiver) key))

(defmethod handle :default
  [receiver & args]
  (apply handle-default receiver args))

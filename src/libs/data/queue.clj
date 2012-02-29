(ns libs.data.queue
  (:refer-clojure :exclude [remove empty])
  (:require [clojure.core :as clj]))

(def empty clojure.lang.PersistentQueue/EMPTY)

(def as-queue #(into empty %))

(defn enqueue [queue evt]
  (conj (as-queue queue) evt))

(def dequeue pop)

(defn remove [pred queue]
  (as-queue (clj/remove pred queue)))

(ns libs.thread
  (:use (libs debug)))

(defmacro spawn [& body]
  `(future (try ~@body
                (catch Throwable e#
                  (handle-error e#)))))

(defn sleep-secs [secs]
  (Thread/sleep (* 1000 secs)))

(defn handle-all-threads []
  (Thread/setDefaultUncaughtExceptionHandler
   (proxy [Thread$UncaughtExceptionHandler] []
       (uncaughtException [thread e] (handle-error e)))))

(ns spec-grind.grind
  (:refer-clojure :exclude [boolean])
  (:require [clojure.spec :as s]))


;; Utils

(defn conform-or-throw [spec x]
  (let [conformed (s/conform spec x)]
    (if (not= ::s/invalid conformed)
      conformed
      (let [^String msg (s/explain-str spec x)]
        (throw (Exception. msg))))))

(defn conformed [spec x]
  (let [result (s/conform spec x)]
    (when (not= ::s/invalid result)
      result)))

(defn explained [spec x]
  (s/explain-str spec x))


;; Grinders

(defmacro simple-or [& preds]
  (let [tags (map keyword
                  (repeatedly (count preds)
                              #(gensym "tag")))
        prepared (interleave tags preds)]
    `(s/and (s/or ~@prepared)
            (s/conformer val))))

(def boolean
  (simple-or
    boolean?
    (s/and
      string?
      (s/conformer
        (fn [x] (case x
                  "true" true
                  "false" false
                  ::s/invalid))))))

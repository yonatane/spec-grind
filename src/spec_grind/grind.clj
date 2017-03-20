(ns spec-grind.grind
  (:refer-clojure :exclude [boolean keyword])
  (:require [clojure.spec :as s]
            [clj-time.coerce]
            [clj-uuid :refer [uuidable? as-uuid]]
            [spec-grind.coerce :refer [as-int as-number as-boolean]]))

(alias 'c 'clojure.core)


;; Utils

(defn conform-or-throw [spec x]
  (let [conformed (s/conform spec x)]
    (if-not (s/invalid? conformed)
      conformed
      (let [^String msg (s/explain-str spec x)]
        (throw (Exception. msg))))))

(defn conformed [spec x]
  (let [result (s/conform spec x)]
    (when-not (s/invalid? result)
      result)))

(defn explained [spec x]
  (s/explain-str spec x))


;; Grinders

(defmacro simple-or [& preds]
  (let [tags (map c/keyword
                  (repeatedly (count preds)
                              #(gensym "tag")))
        prepared (interleave tags preds)]
    `(s/and (s/or ~@prepared)
            (s/conformer val))))

(def boolean
  (simple-or
    boolean?
    (s/and string?
           (s/conformer #(if-some [b (as-boolean %)]
                           b
                           ::s/invalid)))))

(def inst
  (s/and #(satisfies? clj-time.coerce/ICoerce %)
         (s/conformer #(or (clj-time.coerce/to-date %) ::s/invalid))))

(def pos-int
  (simple-or
    pos-int?
    (s/and string?
           (s/conformer #(or (as-int %) ::s/invalid))
           pos-int?)))

(def nat-int
  (simple-or
    nat-int?
    (s/and string?
           (s/conformer #(or (as-int %) ::s/invalid))
           nat-int?)))

(def number
  (simple-or
    number?
    (s/and string?
           (s/conformer #(or (as-number %) ::s/invalid)))))

(def keyword
  (s/conformer #(or (c/keyword %) ::s/invalid)))

(def uuid
  (s/and some? uuidable? (s/conformer as-uuid)))

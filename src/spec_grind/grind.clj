(ns spec-grind.grind
  (:refer-clojure :exclude [boolean keyword])
  (:require [clj-time.coerce]
            [clj-uuid :refer [uuidable? as-uuid]]
            [clojure.spec.alpha :as s]
            [spec-grind.coerce :refer [as-int as-number as-boolean]]
            [spec-grind.impl :as gi]
            [spec-grind.spec :as gs]))

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

(defmacro no-tag-or [& pred-forms]
  (let [pf (mapv gs/res pred-forms)
        pred-forms (vec pred-forms)]
    `(gi/no-tag-or-spec-impl '~pf ~pred-forms)))

(defn keyz-impl [args]
  (let [expected-keys (-> #{}
                          (into (:req-keys args))
                          (into (:opt-keys args)))]
    (s/and
      ;TODO: make a deny spec with explaination.
      #(every? expected-keys (c/keys %))
      (s/map-spec-impl args))))

(defmacro keyz [& {:as args-map}]
  (let [deny (:deny args-map)
        _ (c/assert (or (nil? deny) (= deny :rest)) ":deny arg can only be :rest")
        keys-arg-map (dissoc args-map :deny)
        keys-args (reduce into [] keys-arg-map)
        keys-expanded (macroexpand `(s/keys ~@keys-args))
        map-spec-impl-args (second keys-expanded)]
    `(keyz-impl ~map-spec-impl-args)))

(def boolean
  (no-tag-or
    boolean?
    (s/and string?
           (s/conformer #(if-some [b (as-boolean %)]
                           b
                           ::s/invalid)))))

(def inst
  (s/and #(satisfies? clj-time.coerce/ICoerce %)
         (s/conformer #(or (clj-time.coerce/to-date %) ::s/invalid))))

(def pos-int
  (no-tag-or
    pos-int?
    (s/and string?
           (s/conformer #(or (as-int %) ::s/invalid))
           pos-int?)))

(def nat-int
  (no-tag-or
    nat-int?
    (s/and string?
           (s/conformer #(or (as-int %) ::s/invalid))
           nat-int?)))

(def number
  (no-tag-or
    number?
    (s/and string?
           (s/conformer #(or (as-number %) ::s/invalid)))))

(def keyword
  (s/conformer #(or (c/keyword %) ::s/invalid)))

(def uuid
  (s/and some? uuidable? (s/conformer as-uuid)))

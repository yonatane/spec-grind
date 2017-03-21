(ns spec-grind.spec
  (:refer-clojure :exclude [+ * and assert or cat def keys merge])
  (:require [clojure.spec.gen :as gen]
            [clojure.spec :as s]
            [clojure.walk :as walk]))

(alias 'c 'clojure.core)


;; Private functions copied from clojure.spec as-is or slightly modified.

(defn deep-resolve [k]
  (loop [spec k]
    (if (ident? spec)
      (recur (s/get-spec spec))
      spec)))

(defn reg-resolve
  "returns the spec/regex at end of alias chain starting with k, nil if not found, k if k not ident"
  [k]
  (if (ident? k)
    (let [spec (s/get-spec k)]
      (if-not (ident? spec)
        spec
        (deep-resolve spec)))
    k))

(defn with-name [spec name]
  (cond
    (ident? spec) spec
    (s/regex? spec) (assoc spec ::s/name name)

    (instance? clojure.lang.IObj spec)
    (with-meta spec (assoc (meta spec) ::s/name name))))

(defn spec-name [spec]
  (cond
    (ident? spec) spec

    (s/regex? spec) (::s/name spec)

    (instance? clojure.lang.IObj spec)
    (-> (meta spec) ::s/name)))

(defn maybe-spec
  "spec-or-k must be a spec, regex or resolvable kw/sym, else returns nil."
  [spec-or-k]
  (let [s (c/or (c/and (ident? spec-or-k) (reg-resolve spec-or-k))
                (s/spec? spec-or-k)
                (s/regex? spec-or-k)
                nil)]
    (if (s/regex? s)
      (with-name (s/regex-spec-impl s nil) (spec-name s))
      s)))

(defn the-spec
  "spec-or-k must be a spec, regex or kw/sym, else returns nil. Throws if unresolvable kw/sym"
  [spec-or-k]
  (c/or (maybe-spec spec-or-k)
        (when (ident? spec-or-k)
          (throw (Exception. (str "Unable to resolve spec: " spec-or-k))))))
(defn dt
  ;; Runs conform or a simple predicate and returns the value if valid.
  ([pred x form] (dt pred x form nil))
  ([pred x form cpred?]
   (if pred
     (if-let [spec (the-spec pred)]
       (s/conform spec x)
       (if (ifn? pred)
         (if cpred?
           (pred x)
           (if (pred x) x ::s/invalid))
         (throw (Exception. (str (pr-str form) " is not a fn, expected predicate fn")))))
     x)))

(defn pvalid?
  "internal helper function that returns true when x is valid for spec."
  ([pred x]
   (not (s/invalid? (dt pred x ::s/unknown))))
  ([pred x form]
   (not (s/invalid? (dt pred x form)))))

(defn specize
  ([s] (c/or (s/spec? s) (s/specize* s)))
  ([s form] (c/or (s/spec? s) (s/specize* s form))))

(defn ->sym
  "Returns a symbol from a symbol or var"
  [x]
  (if (var? x)
    (let [^clojure.lang.Var v x]
      (symbol (str (.name (.ns v)))
              (str (.sym v))))
    x))

(defn unfn [expr]
  (if (c/and (seq? expr)
             (symbol? (first expr))
             (= "fn*" (name (first expr))))
    (let [[[s] & form] (rest expr)]
      (conj (walk/postwalk-replace {s '%} form) '[%] 'fn))
    expr))

(defn res [form]
  (cond
    (keyword? form) form
    (symbol? form) (c/or (-> form resolve ->sym) form)
    (sequential? form) (walk/postwalk #(if (symbol? %) (res %) %) (unfn form))
    :else form))

(defn explain-1 [form pred path via in v]
  (let [pred (maybe-spec pred)]
    (if (s/spec? pred)
      (s/explain* pred path (if-let [name (spec-name pred)] (conj via name) via) in v)
      [{:path path :pred (s/abbrev form) :val v :via via :in in}])))

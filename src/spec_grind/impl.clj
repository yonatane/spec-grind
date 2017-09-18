(ns spec-grind.impl
  (:refer-clojure :exclude [+ * and assert or cat def keys merge])
  (:require [clojure.spec.gen.alpha :as gen]
            [clojure.spec.alpha :as s]
            [clojure.walk :as walk]
            [spec-grind.spec :as gs]))

(alias 'c 'clojure.core)


(defn no-tag-or-spec-impl
  ;; Adapted from clojure.spec.
  "Do not call this directly, use 'or'"
  [forms preds]
  (let [specs (mapv gs/specize preds forms)
        cform (fn [x]
                (let [specs specs]
                  (loop [i 0]
                    (if (< i (count specs))
                      (let [spec (specs i)]
                        (let [ret (s/conform* spec x)]
                          (if (s/invalid? ret)
                            (recur (inc i))
                            ret)))
                        ::s/invalid))))]
    (reify
      s/Specize
      (specize* [s] s)
      (specize* [s _] s)

      s/Spec
      (conform* [_ x] (cform x))
      (unform* [_ x] (throw (UnsupportedOperationException. "unform* not implemented")))
      (explain* [this path via in x]
        (when-not (gs/pvalid? this x)
          (apply concat
                 (map (fn [form pred]
                        (when-not (gs/pvalid? pred x)
                          (gs/explain-1 form pred path via in x)))
                      forms preds))))
      (gen* [_ overrides path rmap]
        (throw (UnsupportedOperationException. "gen* not implemented")))
      (with-gen* [_ gfn]
        (throw (UnsupportedOperationException. "with-gen* not implemented")))
      (describe* [_] `(no-tag-or ~@forms)))))

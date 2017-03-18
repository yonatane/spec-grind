(ns spec-grind.coerce
  (:import (java.text NumberFormat ParseException)))


(defn as-int
  ;; Copied from compojure coercions.
  "Parse a string into an integer, or `nil` if the string cannot be parsed."
  [s]
  (try
    (Long/parseLong s)
    (catch NumberFormatException _ nil)))

(defn as-number
  "Parse a string into a number, or `nil` if the string cannot be parsed."
  [s]
  (try
    (-> (NumberFormat/getInstance)
        (.parse s))
    (catch NumberFormatException _ nil)
    (catch ParseException _ nil)))

(defn as-boolean
  [s]
  (case s
    "true" true
    "false" false
    nil))

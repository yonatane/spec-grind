(ns spec-grind.registery
  (:require [spec-grind.grind :as g]
            [clojure.spec :as s]))


(s/def ::g/boolean g/boolean)
(s/def ::g/inst g/inst)

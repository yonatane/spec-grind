(ns spec-grind.registery
  (:require [spec-grind.grind :as g]
            [clojure.spec.alpha :as s]))


(s/def ::g/boolean g/boolean)
(s/def ::g/inst g/inst)
(s/def ::g/pos-int g/pos-int)
(s/def ::g/nat-int g/nat-int)
(s/def ::g/number g/number)
(s/def ::g/keyword g/keyword)
(s/def ::g/uuid g/uuid)

(ns spec-grind.grind-test
  (:require [clojure.spec :as s]
            [clojure.test :refer :all]
            [spec-grind.grind :as g]
            [spec-grind.registery])
  (:import (java.util Date)))


(deftest grinders
  (is (= true (s/conform g/boolean true)))
  (is (= true (s/conform g/boolean "true")))
  (is (= false (s/conform g/boolean false)))
  (is (= false (s/conform g/boolean "false")))
  (is (= ::s/invalid (s/conform g/boolean " true ")))
  (is (= ::s/invalid (s/conform g/boolean nil)))
  (is (= ::s/invalid (s/conform g/boolean 1)))
  (is (= {::g/boolean true}
         (s/conform (s/keys) {::g/boolean "true"})))
  (is (= ::s/invalid
         (s/conform (s/keys) {::g/boolean 1})))

  (is (= #inst"2017-03-18T07:42:34.173-00:00" (s/conform g/inst "2017-03-18T07:42:34.173-00:00")))
  (is (= #inst"2017-03-18T07:42:34.173-00:00" (s/conform g/inst 1489822954173)))
  (is (= #inst"2017-03-18T07:42:34.173-00:00" (s/conform ::g/inst 1489822954173)))
  (is (= ::s/invalid (s/conform g/inst "1489822954173")))
  (is (= ::s/invalid (s/conform g/inst nil)))
  (is (= ::s/invalid (s/conform g/inst {:not-a "date"})))

  (is (= 1 (s/conform ::g/pos-int 1)))
  (is (= 1 (s/conform ::g/pos-int "1")))
  (is (= ::s/invalid (s/conform ::g/pos-int 0)))
  (is (= ::s/invalid (s/conform ::g/pos-int "0")))
  (is (= ::s/invalid (s/conform ::g/pos-int "not a number")))
  (is (= ::s/invalid (s/conform ::g/pos-int 1.5)))
  (is (= ::s/invalid (s/conform ::g/pos-int nil)))

  (is (= 0 (s/conform ::g/nat-int 0)))
  (is (= 0 (s/conform ::g/nat-int "0")))
  (is (= ::s/invalid (s/conform ::g/nat-int -1)))
  (is (= ::s/invalid (s/conform ::g/nat-int "-1")))
  (is (= ::s/invalid (s/conform ::g/nat-int "not a number")))
  (is (= ::s/invalid (s/conform ::g/nat-int 1.5)))
  (is (= ::s/invalid (s/conform ::g/nat-int nil)))

  (is (= 1.5 (s/conform ::g/number 1.5)))
  (is (= 1.5 (s/conform ::g/number "1.5")))
  (is (= -1.5 (s/conform ::g/number "-1.5")))
  (is (= ::s/invalid (s/conform ::g/number "not a number")))
  (is (= ::s/invalid (s/conform ::g/number nil)))

  (is (= :a-key (s/conform ::g/keyword :a-key)))
  (is (= :a-key (s/conform ::g/keyword "a-key")))
  (is (= ::s/invalid (s/conform ::g/keyword 1)))
  (is (= ::s/invalid (s/conform ::g/keyword nil)))

  (are [x y] (= x (s/conform ::g/uuid y))
    #uuid"58cd0579-fa0b-450e-ac6b-92170ba0f296" #uuid"58cd0579-fa0b-450e-ac6b-92170ba0f296"
    #uuid"58cd0579-fa0b-450e-ac6b-92170ba0f296" "58cd0579-fa0b-450e-ac6b-92170ba0f296"
    ::s/invalid "not a uuid"
    ::s/invalid 1
    ::s/invalid nil)

  (is (= "a" (s/conform (g/no-tag-or string? int?) "a")))
  (is (= 1 (s/conform (g/no-tag-or string? int?) 1)))
  (is (= ::s/invalid (s/conform (g/no-tag-or string? int?) :k)))
  )

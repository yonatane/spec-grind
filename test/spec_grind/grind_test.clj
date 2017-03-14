(ns spec-grind.grind-test
  (:require [clojure.spec :as s]
            [clojure.test :refer :all]
            [spec-grind.grind :as g]
            [spec-grind.registery]))


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
  )

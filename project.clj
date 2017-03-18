(defproject spec-grind "0.1.1"
  :description "Treating clojure spec as a meat grinder"
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :url "https://github.com/yonatane/spec-grind"

  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [clj-time "0.13.0"]
                 [danlentz/clj-uuid "0.1.7"]]

  :pedantic? :abort
  :global-vars {*warn-on-reflection* true}
  :target-path "target/%s/")

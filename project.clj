(defproject spec-grind "0.1.0-SNAPSHOT"
  :description "Treating clojure spec as a meat grinder"
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :url "https://github.com/yonatane/spec-grind"

  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]]

  :pedantic? :abort
  :global-vars {*warn-on-reflection* true}
  :target-path "target/%s/")

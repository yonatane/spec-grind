(defproject spec-grind "0.1.2"
  :description "Treating clojure spec as a meat grinder"
  :license {:name "The MIT License"
            :url "https://opensource.org/licenses/MIT"}
  :url "https://github.com/yonatane/spec-grind"

  :dependencies [[org.clojure/clojure "1.9.0-alpha15"]
                 [clj-time "0.13.0"]
                 [danlentz/clj-uuid "0.1.7"]]

  :profiles
  {:dev {:source-paths ["dev"]
         :dependencies [[org.clojure/tools.namespace "0.3.0-alpha3"]
                        [reloaded.repl "0.2.3"]]}}

  :pedantic? :abort
  :global-vars {*warn-on-reflection* true}
  :target-path "target/%s/")

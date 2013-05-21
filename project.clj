(defproject wushuu "0.0.1-SNAPSHOT"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :resource-paths ["multilang"]
  :aot :all
  :repositories [["lib" "file://lib"]]
  :profiles {:dev
              {:dependencies [[storm "0.8.2"]
                              [opencv "245"]]}}
  :min-lein-version "2.1.3"
  )

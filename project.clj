(defproject wushuu "0.0.1-SNAPSHOT"
  :source-paths ["src/clj"]
  :java-source-paths ["src/java"]
  :javac-options ["-target" "1.7" "-source" "1.7"]
  :resource-paths ["res"]
  :aot :all
  :dependencies [
                  [com.google.guava/guava "14.0.1"]
                  [net.java.dev.jna/jna "4.0.0"]
                  [mysql/mysql-connector-java "5.1.25"]
                ]
  :profiles {:dev
              {:dependencies [ 
                               [org.clojure/clojure "1.4.0"]
                               [storm "0.8.2"]
                             ]
              }
            }
  :min-lein-version "2.2.0"
  :plugins [
             [lein-idefiles "0.2.0"]
           ]
  )

(defproject com.wushuu/wushuu "0.0.1-SNAPSHOT"
  :source-paths ["src/clojure"]
  :java-source-paths ["src/java" "test/java"]
  :javac-options ["-target" "1.7" "-source" "1.7"]
  :resource-paths ["src/main/resource"]
  :junit ["test/java"]
  :aot :all
  :dependencies [
                  [com.google.guava/guava "14.0.1"]
                  [net.java.dev.jna/jna "4.0.0"]
                  [mysql/mysql-connector-java "5.1.25"]
                ]
  :profiles {:dev
              {:dependencies [ 
                               [org.clojure/clojure "1.4.0"]
                               [junit/junit "4.11"]
                               [storm "0.8.2"]
                             ]
              }
            }
  :min-lein-version "2.2.0"
  :plugins [
             [lein-idefiles "0.2.0"]
             [lein-junit "1.1.3"]
           ]
  )

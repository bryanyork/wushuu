(defproject com.wushuu/wushuu "0.0.1-SNAPSHOT"
  :source-paths ["src/main/clj"]
  :test-paths ["src/test/clj"]
  :java-source-paths ["src/main/java"]
  :javac-options ["-target" "1.7" "-source" "1.7"]
  :resource-paths ["src/resource" "target/resource"]
  :junit ["src/test/java"]
  :aot :all
  :dependencies [
                  [com.google.guava/guava "14.0.1"]
                  [net.java.dev.jna/jna "4.0.0"]
                  [org.yaml/snakeyaml "1.12"]
                  [mysql/mysql-connector-java "5.1.25"]
                  [org.jdbi/jdbi "2.49"]
                ]
  :profiles { :provided
                { :dependencies [ 
                                  [storm "0.8.2"]
                                ]
                }
              :test
                { :dependencies [ 
                                  [junit/junit "4.11"]
                                ]
                }
            }
  :min-lein-version "2.3.2"
  :plugins [
             [lein-junit "1.1.3"]
           ]
  )

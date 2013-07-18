(defproject wushuu "0.0.1-SNAPSHOT"
  :java-source-paths ["src/jvm"]
  :resource-paths ["res"]
  :jvm-opts ["-Djna.library.path=res/native"]
  :aot :all
  :dependencies [
                  [net.java.dev.jna/jna "4.0.0"]
                  [mysql/mysql-connector-java "5.1.25"]
                  [commons-io/commons-io "2.4"]
                  [org.apache.commons/commons-vfs2 "2.0"]]
  :profiles {:dev
              {:dependencies [ 
                               [org.clojure/clojure "1.4.0"]
                               [storm "0.8.2"]
                             ]}}
  :min-lein-version "2.2.0"
  :plugins [[lein-idefiles "0.2.0"]]
  )

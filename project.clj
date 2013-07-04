(defproject wushuu "0.0.1-SNAPSHOT"
  :source-paths ["src/clj"]
  :java-source-paths ["src/jvm"]
  :native-paths ["src/native"]
  :resource-paths ["resource"]
  :jvm-opts ["-Djna.library.path=/home/jamesf/work/wushuu/src/native/linux-i386"]
  :aot :all
  :dependencies [[net.java.dev.jna/jna "3.5.2"]]
  :profiles {:dev
              {:dependencies [
                               [storm "0.8.2"]
                             ]}}
  :min-lein-version "2.1.3"
  )

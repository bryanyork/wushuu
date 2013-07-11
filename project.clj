(defproject wushuu "0.0.1-SNAPSHOT"
  :java-source-paths ["src/jvm"]
  :resource-paths ["res"]
  :jvm-opts ["-Djna.library.path=/home/jamesf/work/wushuu/res/native"]
  :aot :all
  :dependencies [[net.java.dev.jna/jna "3.5.2"]
                 [org.apache.commons/commons-vfs2 "2.0"]]
  :profiles {:dev
              {:dependencies [
                               [storm "0.8.2"]
                             ]}}
  :min-lein-version "2.2.0"
  )

wushuu
======

Prerequisite
------------
gcc, jdk, lein, cmake, and, most important, internet connection :)

Building
--------
For compiling OpenCV on Linux, you need to get a recent GCC version.

On CentOS, the easiest way to do is to install ["Red Hat DevTools"](http://people.centos.org/tru/devtools-1.1/readme).

1. configure source

        cmake .

2. build native library and dependencies, may take a while

        make

3. build java/clojure source

        make jvm

4. make storm package

        make storm

(ns rinzelight.effects.convolve.kernel-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.effects.convolve.kernel))

(defn create-kernel-by-size
     ([w h]
        (create-kernel-by-size w h (* w h)))
     ([w h size]
        (struct kernel (float-array size)
                       w h)))

(def kernels [[(create-kernel-by-size 4 3)    [false false]]
              [(create-kernel-by-size 3 4)    [false false]]
              [(create-kernel-by-size 3 3 11) [false true]]
              [(create-kernel-by-size 5 5)    [true true]]])

(deftest check-kernel-facts
  (doseq [[kern [valid-r valid-j]] kernels]
    (fact "Checked as a rinzelight struct, it should be valid (or not)"
          (check-kernel kern) => valid-r)
    (fact "Checked as a java Kernel, it should be valid (or not)"
          (check-kernel (to-java-kernel kern)) => valid-j)))

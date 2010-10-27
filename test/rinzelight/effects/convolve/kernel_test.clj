(ns rinzelight.effects.convolve.kernel-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.effects.convolve.kernel))

(defn create-kernel-by-size
     ([w h]
        (create-kernel-by-size w h (* w h)))
     ([w h size]
        (create-kernel w h (float-array size))))

(def kernels [[(create-kernel-by-size 4 3)    [false false]]
              [(create-kernel-by-size 3 4)    [false false]]
              [(create-kernel-by-size 3 3 11) [false true]]
              [(create-kernel-by-size 5 5)    [true true]]])

(def java-kernels (map #(to-java (first %)) kernels))

(deftest valid-kernel-facts
  (doseq [[kern [valid-r valid-j]] kernels]
    (fact "Checked as a rinzelight struct, it should be valid (or not)"
          (valid? kern) => valid-r)
    (fact "Checked as a java Kernel, it should be valid (or not)"
          (valid? (to-java kern)) => valid-j)))

(deftest to-java-facts
  (doseq [kern java-kernels]
    (fact "Returns the very same object if called with a Kernel object"
          (to-java kern) => kern))

  (doseq [[kern _] kernels]
    (let [jk (to-java kern)]
      (fact "It keeps x-orig and y-orig"
            (x-orig jk) => (x-orig kern)
            (y-orig jk) => (y-orig kern)))))

(deftest normalize-kernel-facts

  (doseq [kern java-kernels]
    (fact "Return the same kernel if gamma is zero."
          (normalize-kernel kern) => kern))

  (let [kern (to-java {:width 3
                       :height 3
                       :kernel (float-array 9 1.0)})
        nk   (normalize-kernel kern)]
    (fact "Normalized kernel's gamma must be one"
          (normalize-kernel nk) => nk)))

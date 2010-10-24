(ns rinzelight.effects.rescale-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.effects.rescale))

(deftest complete-multisample-facts
  (let [values '(5
                 [5]
                 [5 5]
                 [5 5 5]
                 [5 5 5 5]
                 (vec (repeat 100 5)))]
    (doseq [v values]
      (fact "It returns always a vector"
            (complete-multisample v 1) => vector?)
      (fact "The vector, has alway four elements."
            (count (complete-multisample v 1)) => 4))))

(ns rinzelight.effects.contrast-stretch-test
  (:use [clojure.test]
        [midje.sweet]
        [rinzelight.effects.contrast-stretch]))

(def string-arguments
     [["hola"             false]
      ["5.34"             false]
      ["344.3%"           false]
      ["24.432%"          false]
      ["hola 34.45%"      false]
      ["hola 34.45% esto" false]
      ["34.45% esto"      false]
      ["5.34%"            true]
      ["34.99%"           true]])

(deftest valid-string-facts
  (doseq [[s v] string-arguments]
    (fact "valid-string? must detect only valid-strings?"
          (valid-string? s) => v)))

(ns rinzelight.format-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.format)
  (:import (javax.imageio ImageReader)))

(deftest normalize-format-facts
  (fact "Upcases any string passed."
        (normalize-format "any-STRING") => "ANY-STRING")
  (fact "If the format is jpg, JPEG is returned."
        (normalize-format "jpg")  => "JPEG"
        (normalize-format "jpEG") => "JPEG"))

(deftest get-format-String-facts
  (fact "Returns the last part of a string splitted by '.'"
        (get-format "any-image.forMat") => "forMat"
        (get-format "any-image.FORmAT") => "FORmAT"))

(comment
  ; Something very bad happens if this get executed.
  (deftest get-format-name-ImageReader-facts
    (let [ir (Object.)
          expected-format "This is a format"]
      (fact "Calls .getFormatName on ImageReader"
            (get-format ir) => expected-format
            (provided
             (class ir) => ImageReader
             (.getFormatName ir) => expected-format)))))


(ns rinzelight.image-test
  (:use midje.sweet)
  (:use clojure.test)
  (:use rinzelight.image)
  (:import (java.awt.image BufferedImage)))

(def img (read-image "samples/clojure.png"))

(deftest create-image-BufferedImage-facts
  (let [w 5
        h 5
        oi (BufferedImage. w h BufferedImage/TYPE_INT_RGB)
        img (create-image oi)]
    (fact "Image should be TYPE_INT_ARGB"
          (.getType (:image img)) => BufferedImage/TYPE_INT_ARGB)
    (fact "It should get a proper format"
          (:format img) => "JPEG")
    (fact "It should get a proper width"
          (:width img) => w)
    (fact "It should get a proper height"
          (:height img) => h)))

; It will be tested through read-image
(deftest create-image-ImageReader-facts
  (let [format "PNG"
        img (read-image "samples/clojure.png")
        w (.getWidth (:image img))
        h (.getHeight (:image img))]
    (fact "Image should be TYPE_INT_ARGB"
          (.getType (:image img)) => BufferedImage/TYPE_INT_ARGB)
    (fact "It should get a proper format"
          (:format img) => format)
    (fact "It should get a proper width"
          (:width img) => w)
    (fact "It should get a proper height"
          (:height img) => h)))

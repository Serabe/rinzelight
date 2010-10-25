(ns wikiscripts.crop
  (:use [rinzelight.image
         :only [read-image
                write-image]]
        [rinzelight.crop]
        [rinzelight.geometry
         :only [center]]))

(def img (read-image "samples/northern-lights.jpg"))

(defmacro crop-ex
  [name & args]
  (let [out-file (str "out/crop-" name ".png")]
    `(write-image (crop ~'img ~@args)
                  ~out-file)))

(crop-ex with-geom 100 50 center)
(crop-ex exceeding-boundaries 100 60 150 130)
(crop-ex do-not-do-this 100 50 100 50)

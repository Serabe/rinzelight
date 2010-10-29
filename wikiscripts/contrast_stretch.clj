(ns wikiscripts.contrast-stretch
  (:use [rinzelight.image
         :only [read-image
                write-image]]
        [rinzelight.effects.contrast-stretch
         :only [contrast-stretch]]
        [rinzelight.effects.normalize
         :only [normalize]]
        [rinzelight.rendering-hints
         :only [rendering-quality]]))

(def img (read-image "samples/northern-lights.jpg"))
(def size (* (:width img) (:height img)))

(defmacro cs-example
  ([bp wp]
     (let [name (str bp "-" wp)]
       `(cs-example ~name ~bp ~wp)))
  ([name bp wp]
     (let [out-file (str "out/cs-" name ".png")]
       `(write-image (contrast-stretch img ~bp ~wp
                                       ~'rendering-quality)
                     ~out-file))))

(cs-example "0.02-0.99" (* 0.02 size) (* 0.99 size))
(cs-example "2%" "99%")
(cs-example "20%" "90%")
(cs-example "0.2-0.9" (* 0.2 size) (* 0.9 size))

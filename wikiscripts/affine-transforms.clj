(ns wikiscripts.at-examples
  (:use [rinzelight.image
         :only [read-image
                write-image]]
        [rinzelight.effects.affine-transforms
         :only [rotate
                scale
                shear
                translate]]
        [rinzelight.rendering-hints]))

(def img (read-image "samples/northern-lights.jpg"))

(defmacro at-example
  [name & code]
  (let [out-file (str "out/at-" (str name) ".png")]
    `(write-image
      (-> img ~@code)
      ~out-file)))

(at-example
 rotate-only-angle
 (rotate (/ Math/PI 3) rendering-quality))

(at-example
 rotate-with-center
 (rotate (/ Math/PI 3) 50 30 rendering-quality))

(at-example
 scale-one-parameter
 (scale 0.5 rendering-quality))

(at-example
 scale-two-parameters
 (scale 0.5 2 rendering-quality))

(at-example
 shear-one-parameter
 (shear 0.8 rendering-quality))

(at-example
 shear-two-parameters
 (shear 0.8 0.5 rendering-quality))

(at-example
 translate-one-parameter
 (translate 25 rendering-quality))

(at-example
 translate-two-parameters
 (translate 25 50 rendering-quality))

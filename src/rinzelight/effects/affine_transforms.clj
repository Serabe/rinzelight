(ns rinzelight.effects.affine-transforms
  (:use [rinzelight.effects.basic-effects
         :only [get-image-for-effect]]
        [rinzelight.rendering-hints
         :only [create-rendering-hint]])
  (:import (java.awt Graphics2D)
           (java.awt.geom AffineTransform)
           (java.awt.image AffineTransformOp)))

(def
 ^{:doc "If the transformation is either a scale, a shear or a combination of both
it is convenient to set this to true in order to resize the new image."}
 *is-a-resize-safe-transform* false)

; TODO: test edge cases
(defn concat-transforms
  "Concatenates all transformations."
  [& transforms]
  (reduce (fn [a b] (doto a (.concatenate b))) (AffineTransform.) transforms))

; TODO: test edge cases.
(defn preconcat-transforms
  "Preconcatenates all transformations"
  [& transforms]
  (reduce (fn [a b] (doto a (.preConcatenate b))) (AffineTransform.) transforms))

(defn new-size-after-transform
  "Works out the size of the resulting image of transforming img with t"
  [img t]
  (let [w (:width img)
        h (:height img)]
    (if *is-a-resize-safe-transform*
      {:width  (+ (* w (.getScaleX t)) (* h (.getShearX t)))
       :height (+ (* h (.getScaleY t)) (* w (.getShearY t)))
       }
      ;else
      {:width w :height h})))

(defn apply-transform
  "Apply transformation t to img. If it contains any scale or shear, new image
will be rescaled accordingly. Rotation and translations wont be considered for
rescaling. rhs are the rendering hints."
  [img t & rhs]
  (let [ns (new-size-after-transform img t)
        ni (get-image-for-effect img
                                 (:width ns)
                                 (:height ns))
        g2 (.getGraphics (:image ni))
        tr (AffineTransformOp. t (apply create-rendering-hint rhs))]
    (doto g2
      (.drawImage (:image img) tr  0 0)
      (.dispose))
    ni))

(defmacro rotate-transform
  "Returns a transformation object for rotating. First argument is the angle.
Second and third (optional) are the center of rotation."
  ([theta]
     `(AffineTransform/getRotateInstance ~theta))
  ([theta center-x center-y]
     `(AffineTransform/getRotateInstance ~theta ~center-x ~center-y)))

(defn rotate
  "Rotates image img. Theta is the angles. If first two numbers in vs are numbers,
they are considered as the center of rotation. The other vs must be rendering
hints."
  [img theta & vs]
  (if (and (number? (nth vs 0))
           (number? (nth vs 1)))
    (let [cx (nth vs 0)
          cy (nth vs 1)
          rhs (drop 2 vs)]
      (apply apply-transform img (rotate-transform theta cx cy) rhs))
    (apply apply-transform img (rotate-transform theta) vs)))

(defmacro scale-transform
  "Returns a transformation object for scaling."
  ([scale-v]
     `(scale-transform ~scale-v ~scale-v))
  ([scale-x scale-y]
     `(AffineTransform/getScaleInstance ~scale-x ~scale-y)))

(defn scale
  "Scales an image img."
  [img scale-x & opt-rhs]
  (let [with-scale-y (number? (first opt-rhs))
        scale-y (if with-scale-y
                  (first opt-rhs)
                  scale-x)
        rhs (if with-scale-y
              (rest opt-rhs)
              opt-rhs)]
    (binding [*is-a-resize-safe-transform* true]
      (apply apply-transform img (scale-transform scale-x scale-y) rhs))))

(defmacro shear-transform
  "Returns a transformation object for shearing."
  ([shear-v]
     `(shear-transform ~shear-v ~shear-v))
  ([shear-x shear-y]
     `(AffineTransform/getShearInstance ~shear-x ~shear-y)))

(defn shear
  "Shears an image img."
  [img shear-x & opt-rhs]
  (let [with-y (number? (first opt-rhs))
        shear-y (if with-y
                  (first opt-rhs)
                  shear-x)
        rhs     (if with-y
                  (rest opt-rhs)
                  opt-rhs)]
    (binding [*is-a-resize-safe-transform* true]
      (apply apply-transform img (shear-transform shear-x shear-y) rhs))))

(defmacro translate-transform
  "Returns a transformation objet for translating."
  ([translation-v]
     `(translate-transform ~translation-v))
  ([translation-x translation-y]
     `(AffineTransform/getTranslateInstance ~translation-x
                                            ~translation-y)))

(defn translate
  "Translates an image."
  [img translation-x & opt-rhs]
  (let [with-y (number? (first opt-rhs))
        translation-y (if with-y
                        (first opt-rhs)
                        translation-x)
        rhs (if with-y
              (rest opt-rhs)
              opt-rhs)]
    (apply apply-transform
           img
           (translate-transform translation-x
                                translation-y)
           rhs)))

(ns rinzelight.composition
  (:use [[rinzelight.image
          :only [clone-image]]])
  (:import (java.awt AlphaComposite)))

(defmacro porter-duff-rule
  "Defines a composite. A composite accepts an optional parameter. If given, it is the factor to multiply the alpha of the source."
  [name]
  (let [field-name (.. (str name) toUpperCase (replace \- \_))
        alpha (symbol (str "AlphaComposite/" field-name))]
    `(defn ~name
       ([] (AlphaComposite/getInstance ~alpha))
       ([factor#] (AlphaComposite/getInstance ~alpha factor#)))))

(porter-duff-rule clear)
(porter-duff-rule dst)
(porter-duff-rule dst-atop)
(porter-duff-rule dst-in)
(porter-duff-rule dst-out)
(porter-duff-rule dst-over)
(porter-duff-rule src)
(porter-duff-rule src-atop)
(porter-duff-rule src-in)
(porter-duff-rule src-out)
(porter-duff-rule src-over)
(porter-duff-rule xor)

(defmacro with-xor-mode
  "Sets the xor mode and restores the original composite after executing body.
Body executes inside a doto bounded to a Graphics2D object."
  [img color & body]
  `(let [ni# (clone-image ~img)
         g2# ^Graphics2D (.. (:image ni#) getGraphics)
         orig-comp# (.. g2# getComposite)]
     (doto g2#
       (.setXORMode ~color)
       ~@body
       (.setComposite orig-comp#))
     ni#))

(defmacro with-composite
  "Sets the composite of the dest to comp, and restores original composite value after executing the body.
Body executes inside a doto bounded to a Graphics2D object."
  [img comp & body]
  (let [eval-comp comp]
    `(let [ni# (clone-image ~img)
           g2# ^Graphics2D (.. (:image ni#) getGraphics)
           orig-comp# (.. g2# getComposite)]
       (doto g2#
         (.setComposite ~eval-comp)
         ~@body
         (.dispose)
         (.setComposite orig-comp#))
       ni#)))

(defn composite
  "Composes src over dst at pixel x,y using comp Composite.
If x,y aren't given, 0,0 is asumed.
If comp is not given, src-over is asumed.
Returns a new image."
  ([src dst] (composite src dst (src-over) 0 0))
  ([src dst comp] (composite src dst comp 0 0))
  ([src dst comp x y]
     (with-composite dst comp
       (.drawImage (:image src) nil x y))))

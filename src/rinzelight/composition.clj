(ns rinzelight.composition
  (:use [rinzelight.image
         :only [clone-image]])
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
  `(let [ni# (clone-image ~img)
         g2# ^Graphics2D (.. (:image ni#) getGraphics)
         orig-comp# (.. g2# getComposite)]
     (doto g2#
       (.setComposite ~comp)
       ~@body
       (.dispose)
       (.setComposite orig-comp#))
     ni#))

(defn composite?
  [o]
  (contains? (ancestors (class o))
             java.awt.Composite))

(defn compose
  "Composes src over dst at pixel x,y using comp Composite.
If x,y aren't given, 0,0 is assumed.
If comp is not given, src-over is assumed.
If instead of two coordinates, one is supplied, it is assumed it is a gravity.
Returns a new image."
  ([dst src] (compose dst src (src-over) 0 0))
  ([dst src comp-or-grav]
     (if (composite? comp-or-grav)
       (compose dst src comp-or-grav 0 0)
       (compose dst src (src-over) comp-or-grav)))
  ([dst src comp grav]
     (let [coord (grav dst src)]
       (compose dst src comp (coord 0) (coord 1))))
  ([dst src comp x y]
     (with-composite dst comp
       (.drawImage (:image src) nil x y))))

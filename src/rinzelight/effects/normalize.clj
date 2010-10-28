(ns rinzelight.effects.normalize
  (:use [rinzelight.effects.contrast-stretch
         :only [contrast-stretch]]))

(defmacro normalize
  "Normalizes an image."
  [img & rhs]
  `(contrast-stretch ~img "2%" "99%" ~@rhs))

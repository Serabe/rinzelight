(ns rinzelight.effects.negate
  (:use [rinzelight.effects.lookup-tables
         :only [invert
                multisample-lookup-table
                apply-lookup-table]]))

(defmacro negate
  "Negates an image."
  [img & rhs]
  `(apply-lookup-table ~img
                       (multisample-lookup-table invert)
                       ~@rhs))

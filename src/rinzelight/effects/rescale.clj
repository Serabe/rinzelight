(ns rinzelight.effects.rescale
  (:use [rinzelight.effects.lookup-tables
         :only [lookup-table-from
                multisample-lookup-table
                apply-lookup-table]]
        [rinzelight.pixel
         :only [pixel-round-to-quantum]]))

; Yes, I do not use RescaleOp.
(defmacro rescale
  [img factor offset & rhs]
  `(apply-lookup-table
    ~img
    (multisample-lookup-table
     (lookup-table-from (fn [~'x]
                          (pixel-round-to-quantum (+ ~offset (* ~factor ~'x))))))
    ~@rhs))

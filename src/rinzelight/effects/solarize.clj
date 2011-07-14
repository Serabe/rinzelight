(ns rinzelight.effects.solarize
  (:use [rinzelight.effects.lookup-tables
         :only [apply-lookup-table
                multisample-lookup-table]]
        [rinzelight.constants
         :only [quantum-range]]
        [rinzelight.pixel
         :only [create-pixel]]
        ))

(defn- solarize-table
  "Creates a solarize table for a channel"
  [threshold]
  (short-array (-> (vector-of :short)
                   (into (range (inc  threshold)))
                   (into (range (- (quantum-range) (inc threshold)) -1 -1)))))

(defn solarize-image
  "Solarizes the image given a threshold"
  [img threshold & rhs]
  (apply apply-lookup-table img
         (multisample-lookup-table (solarize-table threshold))
         rhs))

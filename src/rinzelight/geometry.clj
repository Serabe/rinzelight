(ns rinzelight.geometry
  "Contains geometry implementation.
A geometry modifies some function behaviours, such composition.
There are the following geometries: north, south, east, west, center; and combinations such north-west or south-east.
A geometry is a function that works out coordinates. For example, north would work out the coordinates for src to be centered in the top of dest."
  (:use [rinzelight.image]))

(defn north
  ([dest src]
     (let [dw (:width dest)
           sw (:width src)]
       [(int (/ (- dw sw) 2))
        0])))

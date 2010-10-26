(ns rinzelight.gravity
  "Contains gravity implementation.
A gravityy modifies some function behaviours, such composition.
There are the following gravities: center, north, south, east, west, center; and combinations such north-west or south-east.
A gravity is a function that works out coordinates. For example, north would work out the coordinates for src to be centered in the top of dest."
  (:use rinzelight.image))

(defmacro defgravity
  "Defines a gravity. Parameters are width and height of dest and src.
Actually, any function with two and four parameters returning a two-long int vector would do."
  [name [dst-width dst-height src-width src-height] & body]
  `(defn ~name
     ([~'dst ~'src] (~name (:width ~'dst)
                           (:height ~'dst)
                           (:width ~'src)
                           (:height ~'src)))
     ([~dst-width ~dst-height ~src-width ~src-height]
        ~@body)))

(defmacro compose-gravities
  "Returns a gravity named name whose x coordinate is calculated by grv-x and y coordinate by grv-y"
  [name grv-x grv-y]
  `(defgravity
     ~name [dw# dh# sw# sh#]
     [((~grv-x dw# dh# sw# sh#) 0)
      ((~grv-y dw# dh# sw# sh#) 1)]))

(defgravity center
  [dw dh sw sh]
  [(int (/ (max 0 (- dw sw)) 2))
   (int (/ (max 0 (- dh sh)) 2))])

(defgravity north
  [dw _ sw _]
  [(int (/ (max 0 (- dw sw)) 2))
   0])

(defgravity south
  [dw dh sw sh]
  [(int (/ (max 0 (- dw sw)) 2))
   (int (max 0 (- dh sh)))])

(defgravity east
  [dw dh sw sh]
  [(max 0 (- dw sw))
   (int (/ (max 0 (- dh sh)) 2))])

(defgravity west
  [dw dh sw sh]
  [0
   (int (/ (max 0 (- dh sh)) 2))])

(compose-gravities north-east east north)
(compose-gravities north-west west north)
(compose-gravities south-east east south)
(compose-gravities south-west west south)

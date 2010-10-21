(ns rinzelight.geometry
  "Contains geometry implementation.
A geometry modifies some function behaviours, such composition.
There are the following geometries: north, south, east, west, center; and combinations such north-west or south-east.
A geometry is a function that works out coordinates. For example, north would work out the coordinates for src to be centered in the top of dest."
  (:use [rinzelight.image]))

(defmacro defgeometry
  "Defines a geometry. Parameters are width and height of dest and src.
Actually, any function with two parameters returning a two-long int vector would do."
  [name [dst-width dst-height src-width src-height] & body]
  `(defn ~name
     ([~'dst ~'src] (~name (:width ~'dst)
                           (:height ~'dst)
                           (:width ~'src)
                           (:height ~'src)))
     ([~dst-width ~dst-height ~src-width ~src-height]
        ~@body)))

(defmacro compose-geometries
  "Returns a geometry named name whose x coordinate is calculated by geo-x and y coordinate by geo-y"
  [name geo-x geo-y]
  `(defgeometry
     ~name [dw# dh# sw# sh#]
     [((~geo-x dw# dh# sw# sh#) 0)
      ((~geo-y dw# dh# sw# sh#) 1)]))

(defgeometry north
  [dw _ sw _]
  [(int (/ (max 0 (- dw sw)) 2))
   0])

(defgeometry south
  [dw dh sw sh]
  [(int (/ (max 0 (- dw sw)) 2))
   (int (max 0 (- dh sh)))])

(defgeometry east
  [dw dh sw sh]
  [(max 0 (- dw sw))
   (int (/ (max 0 (- dh sh)) 2))])

(defgeometry west
  [dw dh sw sh]
  [0
   (int (/ (max 0 (- dh sh)) 2))])

(compose-geometries north-east east north)
(compose-geometries north-west west north)
(compose-geometries south-east east south)
(compose-geometries south-west west south)

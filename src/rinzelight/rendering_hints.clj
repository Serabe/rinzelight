(ns rinzelight.rendering-hints
  (:use [clojure.contrib.def :only [defmacro-]])
  (:import (java.awt RenderingHints)))

(defn key-to-value
  [name]
  (cond
   (.endsWith name "ING") (.substring name 0 (- (.length name) (.length "ING")))
   (.endsWith name "_CONTROL") (.substring name 0 (- (.length name) (.length "_CONTROL")))
    :else name))

(defmacro rendering-hint
  [key value]
  (let [key   (str key)
        value (str value)
        k (.. key   toUpperCase (replace \- \_))
        v (.. value toUpperCase (replace \- \_))
        n (symbol (str key "-" value))
        s {(symbol (str "RenderingHints/KEY_" k))
               (symbol (str "RenderingHints/VALUE_" (key-to-value k) "_" v))}]
    `(def ~n ~s)))

(rendering-hint alpha-interpolation default)
(rendering-hint alpha-interpolation speed)
(rendering-hint alpha-interpolation quality)

(rendering-hint antialiasing default)
(rendering-hint antialiasing on)
(rendering-hint antialiasing off)

(rendering-hint color-rendering default)
(rendering-hint color-rendering quality)
(rendering-hint color-rendering speed)

(rendering-hint dithering default)
(rendering-hint dithering disable)
(rendering-hint dithering enable)

(rendering-hint fractionalmetrics default)
(rendering-hint fractionalmetrics on)
(rendering-hint fractionalmetrics off)

(rendering-hint interpolation bicubic)
(rendering-hint interpolation bilinear)
(rendering-hint interpolation nearest-neighbor)

(rendering-hint rendering default)
(rendering-hint rendering quality)
(rendering-hint rendering speed)

(rendering-hint stroke-control default)
(rendering-hint stroke-control normalize)
(rendering-hint stroke-control pure)

(rendering-hint text-antialiasing default)
(rendering-hint text-antialiasing off)
(rendering-hint text-antialiasing on)

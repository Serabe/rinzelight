(ns rinzelight.format
  (:use [clojure.contrib.str-utils2 :only [split]])
  (:import (javax.imageio ImageReader)))

(defn normalize-format
  "Normalizes the string representing a format. Basically, applies toUpperCase and takes JPG to JPEG."
  [#^String format]
  (let [frmt (.toUpperCase format)]
    (if (= frmt "JPG") "JPEG" frmt)))

(defmulti get-format class)

(defmethod get-format String
  [#^String filename]
  (last (split filename #"\.")))

(defmethod get-format ImageReader
  [#^ImageReader reader]
  (.getFormatName reader))

(def get-normalized-format
  (comp normalize-format get-format))
    

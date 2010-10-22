(ns rinzelight.format
  (:import (javax.imageio ImageReader)))

(defn normalize-format
  "Normalizes the string representing a format. Basically, applies toUpperCase and takes JPG to JPEG."
  [#^String format]
  (let [frmt (.toUpperCase format)]
    (if (= frmt "JPG") "JPEG" frmt)))

(defmulti get-format class)

(defmethod get-format String
  [#^String filename]
  (let [idx (inc (.lastIndexOf filename (int \.)))]
    (.substring filename idx)))

(defmethod get-format ImageReader
  [#^ImageReader reader]
  (.getFormatName reader))

(def get-normalized-format
  (comp normalize-format get-format))
    

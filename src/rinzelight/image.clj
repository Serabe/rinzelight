(ns rinzelight.image
  (:use [clojure.contrib.str-utils2 :only [split]]) 
  (:import (java.awt.image BufferedImage)
	   (javax.imageio ImageIO)))

(defstruct image :image :format)

(defn create-image-from-reader
  "Create a new image struct from an ImageReader"
  [reader]
  (let [frmt (.. reader getFormatName toUpperCase)
	img (.read reader 0)]
    (struct image img frmt)))

(defn read-image
  "Reads an image from different resources. Look at ImageIO.createImageInputStream for more info"
  [origin]
  (when origin
    (let [stream (ImageIO/createImageInputStream origin)]
      (when stream
	(with-open [s stream]
	  (let [r (last (iterator-seq (ImageIO/getImageReaders s)))]
	    (when r
	      (.setInput r stream)
	      (create-image-from-reader r))))))))

(defn get-format
  "Gets the format of a string representing the path."
  [filename]
  (last (split filename #"\.")))

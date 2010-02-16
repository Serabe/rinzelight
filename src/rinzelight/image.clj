(ns rinzelight.image
  (:use [clojure.contrib.str-utils2 :only [split]]) 
  (:import (java.awt Color)
	   (java.awt.image BufferedImage)
	   (javax.imageio ImageIO ImageReader)
	   (java.io FileOutputStream)))

(defstruct image :image :format)

(declare width height)

(defn normalize-format
  "Normalizes the string representing a format. Basically,  takes JPG to JPEG"
  [#^String frmt]
  (if (= frmt "JPG") "JPEG" frmt))

(defmulti get-format class)

(defmethod get-format String
  [#^String filename]
  (last (split filename #"\.")))

(defmethod get-format ImageReader
  [#^ImageReader reader]
  (.. reader getFormatName toUpperCase))

(defn get-normalized-format
  "Composes normalize-format and get-format"
  [origin]
  (normalize-format (get-format origin)))

(defn create-image-from-reader
  "Create a new image struct from an ImageReader"
  [reader]
  (let [frmt (get-normalized-format reader)
	img (.read reader 0)]
    (struct image img frmt)))

(declare write-buffered-image)
(defmulti write-image
  "Write an image. It is a multimethod because of problems when writing jpg"
  #(get-normalized-format %2))

(defmethod write-image "JPEG"
  [img uri]
  (let [img-width (width img)
	img-height (height img)
	buf-img (BufferedImage. img-width img-height BufferedImage/TYPE_INT_RGB)]
    (doto (.createGraphics buf-img)
      (.setBackground Color/WHITE)
      (.clearRect 0 0 img-width img-height)
      (.drawImage (:image img) 0 0 nil)
      (.dispose))
    (write-buffered-image buf-img uri)))

(defmethod write-image :default
  [img uri]
  (write-buffered-image (:image img) uri))

; just handy
(defn write-buffered-image
  [buf-img uri]
  "Writes a BufferedImage to a File whose path is uri"
  (with-open [file (FileOutputStream. uri)]
    (ImageIO/write buf-img (get-normalized-format uri) file)))

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

(defn width
  "Returns the width of the image in pixels"
  [img]
  (.getWidth (:image img)))

(defn height
  "Returns the height of the image in pixels"
  [img]
  (.getHeight (:image img)))
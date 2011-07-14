(ns rinzelight.image
  (:use [rinzelight.buffered-image
         :only [write-buffered-image
                create-new-canvas-for-image
                create-empty-canvas]]
        [rinzelight.format :only [get-normalized-format]]
        [rinzelight.display-image :only [display-fn]]) 
  (:import (java.awt Color)
	   (java.awt.image BufferedImage)
	   (javax.imageio ImageIO ImageReader)
	   (java.io FileOutputStream)
           (javax.swing SwingUtilities)))

(defstruct image :image :format :width :height)

(defmacro assure-type
  [t]
  (let [type (symbol (str "BufferedImage/TYPE_INT_"
                          (.toUpperCase (str t))))
        fn-name (symbol (str "assure-" t))]
    `(defn- ~fn-name
       [~'img]
       (let [nbi# (BufferedImage. (.getWidth ~'img)
                                  (.getHeight ~'img)
                                  ~type)]
         (doto (.createGraphics nbi#)
           (.drawImage ~'img 0 0 nil)
           (.dispose))
         nbi#))))

(assure-type argb)
(assure-type rgb)

(defn clone-image
  "Retrieves a new image structure but with a new image cloned from original."
  [img]
  (assoc img
    :image (create-new-canvas-for-image img)))

(defn display-image
  "Display an image"
  [img]
  (when img
    (display-fn img)))

(defmulti create-image
  "Creates a new image from BufferedImage or ImageReader"
  (fn [x & xs]
    (class x)))

(defmethod create-image BufferedImage
  [buf-img]
  (struct image (assure-argb buf-img) "JPEG" (.getWidth buf-img) (.getHeight buf-img)))

(defmethod create-image ImageReader
  [reader]
  (let [frmt (get-normalized-format reader)
	img (assure-argb (.read reader 0))
        width (.getWidth img)
        height (.getHeight img)]
    (struct image img frmt width height)))

(defmethod create-image :default
  ([width height]
     (create-image (create-empty-canvas width height)))
  ([width height color]
     (create-image (create-empty-canvas width height color))))

(defn write-image
  "Write the image img to uri"
  [img uri]
  (let [i (if (= "JPEG" (get-normalized-format uri))
            (assure-rgb (:image img))
            (:image img))]
    (try
      (write-buffered-image i uri)
      true
      (catch Exception e
        false))))

(defmulti read-image
  "Reads an image from different resources. Look at ImageIO.createImageInputStream for more info"
  class)

(defmethod read-image String
  [s]
  (read-image (java.io.File. s)))

; just handy
(defmethod read-image :default
  [origin]
  (when origin
    (let [stream (ImageIO/createImageInputStream origin)]
      (when stream
	(with-open [s stream]
	  (let [r (last (iterator-seq (ImageIO/getImageReaders s)))]
	    (when r
	      (.setInput r stream)
	      (create-image r))))))))

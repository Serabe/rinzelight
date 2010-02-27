(ns rinzelight.image
  (:use [clojure.contrib.str-utils2 :only [split]]
        [rinzelight.buffered-image :only [write-buffered-image
                                          create-new-canvas-for-image]]
        [rinzelight.format :only [get-normalized-format]]
        [rinzelight.display-image :only [display-fn]]) 
  (:import (java.awt Color)
	   (java.awt.image BufferedImage)
	   (javax.imageio ImageIO ImageReader)
	   (java.io FileOutputStream)
           (javax.swing SwingUtilities)))

(defstruct image :image :format :width :height)

(defn display-image
  "Display an image"
  [img]
  (let [runnable #(display-fn img)]
    (if (SwingUtilities/isEventDispatchThread)
      (.run runnable)
      (SwingUtilities/invokeAndWait runnable))))

(defmulti create-image
  "Creates a new image from BufferedImage or ImageReader" class)

(defmethod create-image BufferedImage
  [buf-img]
  (struct image buf-img "JPEG")) ; TODO: Check the format.

(defmethod create-image ImageReader
  [reader]
  (let [frmt (get-normalized-format reader)
	img (.read reader 0)
        width (.getWidth img)
        height (.getHeight img)]
    (struct image img frmt width height)))

(defmulti write-image
  "Write an image. It is a multimethod because of problems when writing jpg"
  #(get-normalized-format %2))

(defmethod write-image "JPEG"
  [img uri]
  (write-image (create-new-canvas-for-image img) uri))

(defmethod write-image :default
  [img uri]
  (write-buffered-image (:image img) uri))

; just handy
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
	      (create-image r))))))))

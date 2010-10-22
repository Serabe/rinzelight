(ns rinzelight.image
  (:use [rinzelight.buffered-image :only [write-buffered-image
                                          create-new-canvas-for-image]]
        [rinzelight.format :only [get-normalized-format]]
        [rinzelight.display-image :only [display-fn]]) 
  (:import (java.awt Color)
	   (java.awt.image BufferedImage)
	   (javax.imageio ImageIO ImageReader)
	   (java.io FileOutputStream)
           (javax.swing SwingUtilities)))

(defstruct image :image :format :width :height)

(comment
  (defn display-image
    "Display an image"
    [img]
    (let [runnable #(display-fn img)]
      (if (SwingUtilities/isEventDispatchThread)
        (.run runnable)
        (SwingUtilities/invokeAndWait runnable)))))

(defn- assure-argb
  [img]
  (let [nbi (BufferedImage. (.getWidth  img)
                            (.getHeight img)
                            BufferedImage/TYPE_INT_ARGB)]
    (doto (.createGraphics nbi)
      (.drawImage img 0 0 nil)
      (.dispose))
    nbi))

(defn clone-image
  "Retrieves a new image structure but with a new image cloned from original."
  [img]
  (assoc img
    :image (create-new-canvas-for-image img)))

(defn display-image
  "Display an image"
  [img]
  (display-fn img))

(defmulti create-image
  "Creates a new image from BufferedImage or ImageReader" class)

(defmethod create-image BufferedImage
  [buf-img]
  (struct image (assure-argb buf-img) "JPEG" (.getWidth buf-img) (.getHeight buf-img))) ; TODO: Check the sizes.

(defmethod create-image ImageReader
  [reader]
  (let [frmt (get-normalized-format reader)
	img (assure-argb (.read reader 0))
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

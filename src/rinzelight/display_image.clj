(ns rinzelight.display-image
  (:import (javax.swing JComponent JFrame WindowConstants)
           (java.awt BorderLayout Dimension)))

(comment
  picture should be the following java code
  new JComponent () {
      @Override
      protected void paintComponent (Graphics g) {
          g.drawImage (image, 0, 0, null);           
      }
  }
  )

(defn- jcomponent
  "Auxiliar function, expects a BufferedImage"
  [img]
  (proxy [JComponent] []
    (paintComponent
     [g]
     (do
       (print "Hola")
       (.drawImage g img 0 0 nil))
     ;(.drawImage g img 0 0 nil)
     )))

(defn- configured-jcomponent
  [img size]
  (doto (jcomponent (:img img))
    (.setPreferredSize size)))

(defn display-fn
  "Just a function for displaying images"
  [img]
  (let [frame (JFrame. "Untitled Image")
        size (Dimension. (:width img) (:height img))
        picture (configured-jcomponent img size)]
    (doto frame
      (.setDefaultCloseOperation WindowConstants/DISPOSE_ON_CLOSE)
      (.setResizable false)
      (.setLayout (BorderLayout.))
      (.add picture BorderLayout/CENTER)
      .pack
      (.setVisible true))))

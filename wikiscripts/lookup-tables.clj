(ns wikiscripts.lookup-tables
  (:use rinzelight.image
        rinzelight.effects.lookup-tables
        rinzelight.rendering-hints))

(def img (read-image "samples/northern-lights.jpg"))

(defmacro lt-example
  [img name]
  (let [out-file (str "out/lt-" name ".png")]
    `(write-image (apply-lookup-table ~img
                                      (multisample-lookup-table ~name)
                                      color-rendering-quality
                                      antialiasing-on
                                      rendering-quality)
                  ~out-file)))

(lt-example img zero)
(lt-example img straight)
(lt-example img invert)
(lt-example img brighten)
(lt-example img better-brighten)
(lt-example img posterize)

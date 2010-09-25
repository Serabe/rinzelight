# RinzeLight

RinzeLight is an image processing library inspired and based on RMagick4J.

# Usage

1.- Read an image. It can be read from a lot of sources.

    (def img (read-image "path-to-an-image"))

2.- Display it.

    (display-image img)

3.- Save it.

    (write-image img "another-path")

# Modifying an image

Currently, there are only two methods to modify an image:
__map-image__ and __map-pixel-location__.

## map-image

__map-image__ accepts two parameters: a function and an image. The
function receives a pixel structure and must return a pixel.

For example, for inverting an image __img__ you can use the following
code:

    (use 'rinzelight.effects.basic-effects)
    (use 'rinzelight.pixel)

    (map-image invert-pixel img)

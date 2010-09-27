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

Currently, there are only three methods to modify an image:
__map-image__,  __map-pixel-location__ and __apply-lookup-table__.

## map-image

__map-image__ accepts two parameters: a function and an image. The
function receives a pixel structure and must return a pixel.

For example, for inverting an image __img__ you can use the following
code:

    (use 'rinzelight.effects.basic-effects)
    (use 'rinzelight.pixel)

    (map-image invert-pixel img)

## map-pixel-location

__map-pixel-location__ receives either two or four parameters. If
final image has the same width and height as the original, only img
and function is needed. Otherwise, new width and height are
needed. The function receives a two-length vector representing current
pixel location and must return a two-length vector representing new
location for current pixel.

You can use functions in *rinzelight.effects.helper-functions* for
some basic effects, like rotating left and right. See examples dir for
more of these functions.

    (def img (read-image "samples/northern-lights.clj"))

    (def new-size (vertical-flip-new-size img))

    (map-pixel-location (vertical-flip img) img
                        (new-size 0)
                        (new-size 1))

## apply-lookup-table

__apply-lookup-table__ accepts two parameters: an image and a lookup
table. Then, it applies the lookup table to the image and return a new
one.

There are a few one-dimensional lookup tables:

  * zero: Returns zero for any value.
  * straight: Returns the same value it receives.
  * invert: Returns quantum-range - x, being x the received value.
  * brighten: It is used to brighten the image.
  * better-brighten: It produces a better brightened image.
  * posterize: Used for posterizing an image.

For creating a multidimensional lookup-table, see:

    (doc multisample-lookup-table)

It is really easy.

You can create a lookup table from a pixel function, but take into
account that result may differ from what you expected.

For applying a lookup-table, just use __apply-lookup-table__ like
this:

    (def img (read-image "samples-northern-lights")    

    (def only-green (multisample-lookup-table zero
                                              zero
                                              straight))

    (apply-lookup-table img only-green)

If a pixel function can be expressed as a lookup table, use this
method since it is way faster than map-image.

#include "image.h"

#pragma once
#ifndef IMAGE_TRANSFORMER_H
#define IMAGE_TRANSFORMER_H

struct image *gen_processed_image(struct image *source_image);

struct image *gen_processed_image_simd(struct image *source_image);

#endif

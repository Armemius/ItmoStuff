#include "angles.h"
#include "image.h"

#pragma once
#ifndef IMAGE_TRANSFORMER_H
#define IMAGE_TRANSFORMER_H

struct image* gen_rotated_image(struct image* source_image, enum ANGLES angle);

#endif

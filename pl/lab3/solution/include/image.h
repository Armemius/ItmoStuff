#include "pixel.h"

#include <stdint.h>

#pragma once
#ifndef IMAGE_H
#define IMAGE_H

struct image {
	uint32_t width;
	uint32_t height;
	struct pixel** data;
};

struct image* gen_empty_image(const uint32_t width, const uint32_t height);

void free_image_resources(struct image* image_ptr);

#endif

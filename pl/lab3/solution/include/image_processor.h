#include "image.h"
#include "image_signatures.h"

#include <stddef.h>

#pragma once
#ifndef IMAGE_PROCESSOR_H
#define IMAGE_PROCESSOR_H

#define GENERATION_FAILURE ((size_t)-1)

struct image* parse_image(const unsigned char* image_data, const enum image_signatures signature);

struct image* parse_bmp_image(const unsigned char* image_data);

size_t generate_image(unsigned char** buffer, const struct image* input_image, const enum image_signatures signature);

size_t generate_bmp_image(unsigned char** buffer, const struct image* input_image);

#endif

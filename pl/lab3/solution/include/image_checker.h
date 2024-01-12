#include "image_signatures.h"

#include <stdbool.h>
#include <stddef.h>

#pragma once
#ifndef TYPE_CHECKER_H
#define TYPE_CHECKER_H

enum image_validation_status {
	IMAGE_VALIDATION_OK,
	IMAGE_VALIDATION_UNSUPPORTED_FORMAT,
	IMAGE_VALIDATION_BMP_CORRUPTED_HEADER,
	IMAGE_VALIDATION_BMP_CORRUPTED_DATA,
	IMAGE_VALIDATION_BMP_UNSUPPORTED_COLOR_DEPTH
};

enum image_signatures check_image_signature(const unsigned char* image_data, const size_t data_size);

bool check_bmp_signature(const unsigned char* image_data, const size_t data_size);

enum image_validation_status validate_image_data(const unsigned char* image_data, const size_t data_size, const enum image_signatures signature);

enum image_validation_status validate_bmp_data(const unsigned char* image_data, const size_t data_size);

#endif

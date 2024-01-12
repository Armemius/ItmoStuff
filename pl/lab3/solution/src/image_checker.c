#include "image_checker.h"

#include "headers.h"
#include "image_signatures.h"

#include <stdbool.h>
#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

enum image_signatures check_image_signature(const unsigned char* image_data, const size_t data_size) {
	if (check_bmp_signature(image_data, data_size)) {
		return BMP_SIGNATURE;
	}
	return UNKNOWN_SIGNATURE;
}

bool check_bmp_signature(const unsigned char* image_data, const size_t data_size) {
	if (data_size < sizeof(uint16_t)) {
		return false;
	}
    return *(uint16_t*)image_data == BMP_SIGNATURE_CODE;
}

enum image_validation_status validate_image_data(const unsigned char* image_data, const size_t data_size, const enum image_signatures signature) {
    if (signature == BMP_SIGNATURE) {
        return validate_bmp_data(image_data, data_size);
    }
    return IMAGE_VALIDATION_UNSUPPORTED_FORMAT;
}

enum image_validation_status validate_bmp_data(const unsigned char* image_data, const size_t data_size) {
	if (data_size < sizeof(struct bmp_header)) {
        return IMAGE_VALIDATION_BMP_CORRUPTED_HEADER;
	}

	struct bmp_header* header = (struct bmp_header*)image_data;
	
    if (header->bfile_size != data_size) {
        return IMAGE_VALIDATION_BMP_CORRUPTED_DATA;
    }

    if (header->b_offset_bits < BMP_HEADER_SIZE || header->b_offset_bits >= data_size) {
        return IMAGE_VALIDATION_BMP_CORRUPTED_HEADER;
    }

    if (header->bi_size != BMP_INFORMATION_HEADER_SIZE
        && header->bi_size != BMP_EXTENDED_HEADER_SIZE
        && header->bi_size != BMP_MAX_HEADER_SIZE) {
        return IMAGE_VALIDATION_BMP_CORRUPTED_HEADER;
    }

    if (header->bi_width <= 0 || header->bi_height <= 0) {
        return IMAGE_VALIDATION_BMP_CORRUPTED_HEADER;
    }

    if (header->bi_color_depth != BMP_COLOR_DEPTH) {
        return IMAGE_VALIDATION_BMP_UNSUPPORTED_COLOR_DEPTH;
    }

    if (header->bi_compression != BMP_COMPRESSION 
        && header->bi_compression != BMP_MAX_COMPRESSION) {
        return IMAGE_VALIDATION_BMP_CORRUPTED_HEADER;
    }

    return IMAGE_VALIDATION_OK;
}

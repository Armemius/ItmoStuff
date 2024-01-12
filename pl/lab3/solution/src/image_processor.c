#include "image_processor.h"

#include "headers.h"
#include "image.h"
#include "image_signatures.h"

#include <stdint.h>
#include <stdio.h>
#include <stdlib.h>

struct image* parse_image(const unsigned char* image_data, const enum image_signatures signature) {
	if (signature == BMP_SIGNATURE) {
		return parse_bmp_image(image_data);
	}
	return NULL;
}

struct image* parse_bmp_image(const unsigned char* image_data) {
	struct bmp_header* header = (void*)image_data;
	const unsigned char* pixel_data = image_data + header->b_offset_bits;
	uint32_t height = header->bi_height;
	uint32_t width = header->bi_width;
	uint8_t offset_bytes = BMP_LINE_MULTIPLICITY - width * sizeof(struct pixel) % BMP_LINE_MULTIPLICITY;

	struct image* result = gen_empty_image(width, height);
	if (result == NULL) {
		return NULL;
	}

	for (size_t it = 0; it < height; ++it) {
		for (size_t jt = 0; jt < width; ++jt) {
			size_t vertical_offset = it * (width * sizeof(struct pixel) + offset_bytes);
			size_t horizontal_offset = jt * sizeof(struct pixel);
			struct pixel* pixel_ptr = (void*)(pixel_data + vertical_offset + horizontal_offset);
			result->data[jt][height - it - 1] = *pixel_ptr;
		}
	}
	return result;
}

size_t generate_image(unsigned char** buffer, const struct image* input_image, const enum image_signatures signature) {
	if (signature == BMP_SIGNATURE) {
		return generate_bmp_image(buffer, input_image);
	}
	return GENERATION_FAILURE;
}

size_t generate_bmp_image(unsigned char** buffer, const struct image* input_image) {
	uint8_t offset_bytes = BMP_LINE_MULTIPLICITY - input_image->width * sizeof(struct pixel) % BMP_LINE_MULTIPLICITY;
	size_t calculated_size = sizeof(struct bmp_header) + (input_image->width * sizeof(struct pixel) + offset_bytes) * input_image->height;
	*buffer = calloc(calculated_size, sizeof(unsigned char));
	if (*buffer == NULL) {
		return GENERATION_FAILURE;
	}
	struct bmp_header* header = (void*)*buffer;
	header->bf_type = BMP_SIGNATURE_CODE;
	header->bfile_size = (uint32_t)calculated_size;
	header->bf_reserved = 0;
	header->b_offset_bits = sizeof(struct bmp_header);
	header->bi_size = BMP_INFORMATION_HEADER_SIZE;
	header->bi_width = input_image->width;
	header->bi_height = input_image->height;
	header->bi_planes = BMP_COLOR_PLANES;
	header->bi_color_depth = BMP_COLOR_DEPTH;
	header->bi_compression = BMP_COMPRESSION;
	header->bi_size_image = input_image->width * input_image->height * sizeof(struct pixel);
	header->bi_x_pels_per_meter = BMP_PELS_PER_METER;
	header->bi_y_pels_per_meter = BMP_PELS_PER_METER;
	header->bi_clr_used = BMP_CLR_USED;
	header->bi_clr_important = BMP_CLR_IMPORTANT;

	unsigned char* image_data = *buffer + sizeof(struct bmp_header);
	for (size_t it = 0; it < input_image->height; ++it) {
		for (size_t jt = 0; jt < input_image->width; ++jt) {
			size_t vertical_offset = it * (input_image->width * sizeof(struct pixel) + offset_bytes);
			size_t horizontal_offset = jt * sizeof(struct pixel);
			struct pixel* pixel_ptr = (void*)(image_data + vertical_offset + horizontal_offset);
			*pixel_ptr = input_image->data[jt][input_image->height - it - 1];
		}
	}

	return calculated_size;
}

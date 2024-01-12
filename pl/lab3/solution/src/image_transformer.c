#include "image_transformer.h"

#include "angles.h"
#include "image.h"

#include <stdio.h>
#include <stdlib.h>

static struct image* gen_rotated_image_0_deg(struct image* source_image) {
	struct image* result = gen_empty_image(source_image->width, source_image->height);
	if (result == NULL) {
		return NULL;
	}
	for (size_t it = 0; it < source_image->width; ++it) {
		for (size_t jt = 0; jt < source_image->height; ++jt) {
			result->data[it][jt] = source_image->data[it][jt];
		}
	}
	return result;
}

static struct image* gen_rotated_image_90_deg(struct image* source_image) {
	struct image* result = gen_empty_image(source_image->height, source_image->width);
	if (result == NULL) {
		return NULL;
	}
	for (size_t it = 0; it < source_image->width; ++it) {
		for (size_t jt = 0; jt < source_image->height; ++jt) {
			result->data[source_image->height - jt - 1][it] = source_image->data[it][jt];
		}
	}
	return result;
}

static struct image* gen_rotated_image_180_deg(struct image* source_image) {
	struct image* result = gen_empty_image(source_image->width, source_image->height);
	if (result == NULL) {
		return NULL;
	}
	for (size_t it = 0; it < source_image->width; ++it) {
		for (size_t jt = 0; jt < source_image->height; ++jt) {
			result->data[source_image->width - it - 1][source_image->height - jt - 1] = source_image->data[it][jt];
		}
	}
	return result;
}

static struct image* gen_rotated_image_270_deg(struct image* source_image) {
	struct image* result = gen_empty_image(source_image->height, source_image->width);
	if (result == NULL) {
		return NULL;
	}
	for (size_t it = 0; it < source_image->width; ++it) {
		for (size_t jt = 0; jt < source_image->height; ++jt) {
			result->data[jt][source_image->width - it - 1] = source_image->data[it][jt];
		}
	}
	return result;
}

struct image* gen_rotated_image(struct image* source_image, enum ANGLES angle) {
	switch (angle) {
		case ANGLE_0:
			return gen_rotated_image_0_deg(source_image);
		case ANGLE_90:
			return gen_rotated_image_90_deg(source_image);
		case ANGLE_180:
			return gen_rotated_image_180_deg(source_image);
		case ANGLE_270:
			return gen_rotated_image_270_deg(source_image);
		default:
			return NULL;
	}
}


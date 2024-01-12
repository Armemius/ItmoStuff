#include "image.h"

#include "pixel.h"

#include <stdio.h>
#include <stdlib.h>

struct image* gen_empty_image(const uint32_t width, const uint32_t height) {
	struct image* result = malloc(sizeof(struct image));
	if (result == NULL) {
		return NULL;
	}
	result->data = malloc(sizeof(struct pixel*) * width);
	if (result->data == NULL) {
		free(result);
		return NULL;
	}

	for (size_t it = 0; it < width; ++it) {
		result->data[it] = malloc(sizeof(struct pixel) * height);
		if (result->data + it == NULL) {
			free_image_resources(result);
			return NULL;
		}
	}

	result->width = width;
	result->height = height;

	return result;
}

void free_image_resources(struct image* image_ptr) {
	for (size_t it = 0; it < image_ptr->width; ++it) {
		if (image_ptr->data + it != NULL) {
			free(image_ptr->data[it]);
		}
	}
	free(image_ptr->data);
	free(image_ptr);
}

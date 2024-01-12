#include "image_transformer.h"

#include "image.h"

#include <math.h>
#include <stdio.h>
#include <stdlib.h>

struct image *gen_processed_image(struct image *source_image) {
  struct image *result = source_image;

  for (size_t it = 0; it < source_image->width; ++it) {
    for (size_t jt = 0; jt < source_image->height; ++jt) {
      uint8_t r_input = source_image->data[it][jt].r;
      uint8_t g_input = source_image->data[it][jt].g;
      uint8_t b_input = source_image->data[it][jt].b;

      double r_processed =
          (r_input * .393) + (g_input * .769) + (b_input * .189);
      double g_processed =
          (r_input * .349) + (g_input * .686) + (b_input * .168);
      double b_processed =
          (r_input * .272) + (g_input * .534) + (b_input * .131);

      result->data[it][jt].r =
          r_processed > 0xFF ? 0xFF : (uint8_t)(r_processed);
      result->data[it][jt].g =
          g_processed > 0xFF ? 0xFF : (uint8_t)(g_processed);
      result->data[it][jt].b =
          b_processed > 0xFF ? 0xFF : (uint8_t)(b_processed);
    }
  }
  return result;
}

#define _POSIX_C_SOURCE 199309L

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>

#include "exceptions_handler.h"
#include "file_io.h"
#include "image_checker.h"
#include "image_processor.h"
#include "image_transformer.h"
#include "validator.h"

int main(int argc, char **argv) {
  enum args_validation_status args_status = validate(argc, argv);
  if (args_status != ARGS_VALIDATION_OK) {
    process_args_validation_failure(args_status);
  }

  const char *input_file = argv[1];
  const char *output_file = argv[2];
  bool use_simd = false;

  if (argc == MAX_ARGS_COUNT) {
    if (strcmp(argv[3], SIMD_FLAG) == 0) {
      use_simd = true;
    }
  }

  unsigned char *image_data;
  size_t data_size = fread_binary(input_file, &image_data);
  if (data_size == FILE_JOB_FAILURE || image_data == NULL) {
    fprintf(stderr, "Error occurred while reading file %s", input_file);
    exit(EXIT_FAILURE);
  }

  enum image_signatures signature =
      check_image_signature(image_data, data_size);
  enum image_validation_status image_status =
      validate_image_data(image_data, data_size, signature);
  if (image_status != IMAGE_VALIDATION_OK) {
    process_image_validation_failure(image_status);
  }

  struct image *opened_image = parse_image(image_data, signature);
  if (opened_image == NULL) {
    fprintf(stderr, "Unable to parse image data\n");
    exit(EXIT_FAILURE);
  }
  free(image_data);

  struct image *transformed_image;
  struct timespec transformation_start = {0, 0}, transformation_end = {0, 0};
  clock_gettime(CLOCK_MONOTONIC, &transformation_start);
  if (use_simd) {
    transformed_image = gen_processed_image_simd(opened_image);
  } else {
    transformed_image = gen_processed_image(opened_image);
  }
  clock_gettime(CLOCK_MONOTONIC, &transformation_end);

  if (opened_image == NULL) {
    fprintf(stderr, "Unable to parse image data\n");
    exit(EXIT_FAILURE);
  }

  unsigned char *transformed_image_data;
  size_t transformed_image_size =
      generate_image(&transformed_image_data, transformed_image, signature);
  if (transformed_image_size == GENERATION_FAILURE) {
    fprintf(stderr, "Unable to generate image\n");
    exit(EXIT_FAILURE);
  }
  free_image_resources(transformed_image);

  size_t bytes_written = fwrite_binary(output_file, transformed_image_data,
                                       transformed_image_size);
  if (bytes_written == FILE_JOB_FAILURE) {
    fprintf(stderr, "Error occurred while writing to file %s", output_file);
    exit(EXIT_FAILURE);
  }
  free(transformed_image_data);

  printf("%zu", transformation_end.tv_nsec - transformation_start.tv_nsec);
  exit(EXIT_SUCCESS);
}

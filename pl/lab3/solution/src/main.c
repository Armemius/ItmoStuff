#include "exceptions_handler.h"
#include "file_io.h"
#include "image_checker.h"
#include "image_processor.h"
#include "image_transformer.h"
#include "validator.h"

#include <stdlib.h>

#define FULL_ROTATION_DEGREES 360

int main(int argc, char** argv) {
    enum args_validation_status args_status = validate(argc, argv);
    if (args_status != ARGS_VALIDATION_OK) {
        process_args_validation_failure(args_status);
    }

    const char* input_file = argv[1];
    const char* output_file = argv[2];
    const int rotation_angle = (atoi(argv[3]) + FULL_ROTATION_DEGREES) % FULL_ROTATION_DEGREES;

    unsigned char* image_data;
    size_t data_size = fread_binary(input_file, &image_data);
    if (data_size == FILE_JOB_FAILURE || image_data == NULL) {
        fprintf(stderr, "Error occured while reading file %s", input_file);
        exit(EXIT_FAILURE);
    }

    enum image_signatures signature = check_image_signature(image_data, data_size);
    enum image_validation_status image_status = validate_image_data(image_data, data_size, signature);
    if (image_status != IMAGE_VALIDATION_OK) {
        process_image_validation_failure(image_status);
    }

    struct image* opened_image = parse_image(image_data, signature);
    if (opened_image == NULL) {
        fprintf(stderr, "Unable to parse image data\n");
        exit(EXIT_FAILURE);
    }
    free(image_data);

    struct image* transformed_image = gen_rotated_image(opened_image, rotation_angle);
    if (opened_image == NULL) {
        fprintf(stderr, "Unable to parse image data\n");
        exit(EXIT_FAILURE);
    }
    free_image_resources(opened_image);

    unsigned char* transformed_image_data;
    size_t transformed_image_size = generate_image(&transformed_image_data, transformed_image, signature);
    if (transformed_image_size == GENERATION_FAILURE) {
        fprintf(stderr, "Unable to generate image\n");
        exit(EXIT_FAILURE);
    }
    free_image_resources(transformed_image);

    size_t bytes_written = fwrite_binary(output_file, transformed_image_data, transformed_image_size);
    if (bytes_written == FILE_JOB_FAILURE) {
        fprintf(stderr, "Error occured while writin to file %s", output_file);
        exit(EXIT_FAILURE);
    }
    free(transformed_image_data);

    exit(EXIT_SUCCESS);
}

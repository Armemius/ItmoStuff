#include "exceptions_handler.h"

#include "validator.h"

#include <stdio.h>
#include <stdlib.h>

void process_args_validation_failure(const enum args_validation_status args_status) {
    if (args_status == ARGS_VALIDATION_WRONG_ARG_COUNT) {
        fprintf(stderr, "Invalid number of arguments\n"
            "Usage: image-transformer <source-image> <transformed-image> <angle>\n");
        exit(EXIT_FAILURE);
    }
    if (args_status == ARGS_VALIDATION_ANGLE_PARSE_ERROR) {
        fprintf(stderr, "Angle value is not a number\n");
        exit(EXIT_FAILURE);
    }
    if (args_status == ARGS_VALIDATION_INVALID_ANGLE) {
        fprintf(stderr, "Angle value is not acceptable\n"
            "Value should be one of: 0, 90, -90, 180, -180, 270, -270\n");
        exit(EXIT_FAILURE);
    }
}

void process_image_validation_failure(const enum image_validation_status image_status) {
    if (image_status == IMAGE_VALIDATION_UNSUPPORTED_FORMAT) {
        fprintf(stderr, "Input file format is unsupported\n");
        exit(EXIT_FAILURE);
    }
    if (image_status == IMAGE_VALIDATION_BMP_CORRUPTED_HEADER) {
        fprintf(stderr, "Input BMP file has corrupted header\n");
        exit(EXIT_FAILURE);
    }
    if (image_status == IMAGE_VALIDATION_BMP_CORRUPTED_DATA) {
        fprintf(stderr, "Input BMP file is corrupted\n");
        exit(EXIT_FAILURE);
    }
    if (image_status == IMAGE_VALIDATION_BMP_UNSUPPORTED_COLOR_DEPTH) {
        fprintf(stderr, "Unsupported BMP color depth (only 24 bits depth allowed)\n");
        exit(EXIT_FAILURE);
    }
}

#include <stdbool.h>

#pragma once
#ifndef VALIDATOR_H
#define VALIDATOR_H

#define MIN_ARGS_COUNT 3
#define MAX_ARGS_COUNT 4

#define SIMD_FLAG "--simd"

enum args_validation_status {
  ARGS_VALIDATION_WRONG_ARG_COUNT,
  ARGS_VALIDATION_WRONG_FLAG,
  ARGS_VALIDATION_OK
};

enum args_validation_status validate(const int argc, char **argv);

#endif

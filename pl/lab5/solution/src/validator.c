#include "validator.h"

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

enum args_validation_status validate(const int argc, char **argv) {
  if (!(MIN_ARGS_COUNT <= argc || argc <= MAX_ARGS_COUNT)) {
    return ARGS_VALIDATION_WRONG_ARG_COUNT;
  }
  if (argc == MAX_ARGS_COUNT) {
    if (strcmp(argv[3], SIMD_FLAG) != 0) {
      return ARGS_VALIDATION_WRONG_FLAG;
    }
  }
  return ARGS_VALIDATION_OK;
}

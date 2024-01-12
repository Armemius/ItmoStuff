#include <stdbool.h>

#pragma once
#ifndef VALIDATOR_H
#define VALIDATOR_H

enum args_validation_status {
	ARGS_VALIDATION_WRONG_ARG_COUNT,
	ARGS_VALIDATION_ANGLE_PARSE_ERROR,
	ARGS_VALIDATION_INVALID_ANGLE,
	ARGS_VALIDATION_OK
};

enum args_validation_status validate(const int argc, char** argv);

bool check_number(const char* raw_number);

bool validate_number(const int number);

#endif

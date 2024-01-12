#include "validator.h"

#include <stdbool.h>
#include <stdio.h>
#include <stdlib.h>

#define ARGS_COUNT 4

enum args_validation_status validate(const int argc, char** argv) {
	if (argc != ARGS_COUNT) {
		return ARGS_VALIDATION_WRONG_ARG_COUNT;
	}
	if (!check_number(argv[3])) {
		return ARGS_VALIDATION_ANGLE_PARSE_ERROR;
	}
	if (!validate_number(atoi(argv[3]))) {
		return ARGS_VALIDATION_INVALID_ANGLE;
	}
	return ARGS_VALIDATION_OK;
}

bool check_number(const char* raw_number) {
	bool number_flag = false;
	if (*raw_number == '-') {
		++raw_number;
	}
	while (*raw_number != 0) {
		if (*raw_number < '0' || '9' < *raw_number) {
			return false;
		}
		++raw_number;
		number_flag = true;
	}
	return number_flag;
}

bool validate_number(const int number) {
	switch (number) {
		case 0:
		case 90:
		case -90:
		case 180:
		case -180:
		case 270:
		case -270:
			return true;
		default:
			return false;
	}
}

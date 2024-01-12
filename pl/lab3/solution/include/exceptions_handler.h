#include "image_checker.h"
#include "validator.h"

#pragma once
#ifndef EXCEPTIONS_HANDLER_H
#define EXCEPTIONS_HANDLER_H

void process_args_validation_failure(const enum args_validation_status args_status);

void process_image_validation_failure(const enum image_validation_status image_status);

#endif

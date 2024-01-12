#include <stddef.h>

#pragma once
#ifndef FILE_IO_H
#define FILE_IO_H

#define WRITE "wb"
#define READ "rb"

#define FILE_JOB_FAILURE ((size_t)-1)

size_t fread_binary(const char *filename, unsigned char **buffer);

size_t fwrite_binary(const char *filename, const unsigned char *buffer,
                     const size_t buffer_size);

#endif

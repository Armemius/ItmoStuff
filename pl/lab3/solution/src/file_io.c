#include "file_io.h"

#include <errno.h>
#include <malloc.h>
#include <stdio.h>
#include <stdlib.h>

size_t fread_binary(const char* filename, unsigned char** buffer) {
	FILE* file = fopen(filename, READ);

	if (file == NULL) {
		return FILE_JOB_FAILURE;
	}

	fseek(file, 0, SEEK_END);
	size_t size = ftell(file);
	fseek(file, 0, SEEK_SET);
	
	*buffer = calloc(size, sizeof(unsigned char));

	if (*buffer == NULL) {
		return FILE_JOB_FAILURE;
	}

	if (fread(*buffer, sizeof(unsigned char), size, file) != size) {
		fflush(file);
		fclose(file);
		return FILE_JOB_FAILURE;
	}
	fflush(file);
	if (fclose(file)) {
		return FILE_JOB_FAILURE;
	}

	return size;
}

size_t fwrite_binary(const char* filename, const unsigned char* buffer, const size_t buffer_size) {
	FILE* file = fopen(filename, WRITE);
	if (file == NULL) {
		return FILE_JOB_FAILURE;
	}

	size_t bytes_written = fwrite(buffer, sizeof(unsigned char), buffer_size, file);
	fflush(file);
	if (fclose(file)) {
		return FILE_JOB_FAILURE;
	}

	return bytes_written;
}

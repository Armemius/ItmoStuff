#include <stdint.h>

#pragma once
#ifndef PIXEL_H
#define PIXEL_H

#pragma pack(push, 1)
struct pixel {
	uint8_t b;
	uint8_t g;
	uint8_t r;
};
#pragma pack(pop)

#endif

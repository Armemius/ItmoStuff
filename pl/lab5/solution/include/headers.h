#include <stdint.h>

#pragma once
#ifndef HEADERS_H
#define HEADERS_H

#define BMP_LINE_MULTIPLICITY 4
#define BMP_SIGNATURE_CODE 0x4D42
#define BMP_INFORMATION_HEADER_SIZE 40
#define BMP_EXTENDED_HEADER_SIZE 108
#define BMP_MAX_HEADER_SIZE 124
#define BMP_COLOR_PLANES 1
#define BMP_COLOR_DEPTH 24
#define BMP_HEADER_SIZE 14
#define BMP_COMPRESSION 0
#define BMP_MAX_COMPRESSION 3
#define BMP_PELS_PER_METER 0xB12
#define BMP_CLR_USED 0
#define BMP_CLR_IMPORTANT 0

#pragma pack(push, 1)
struct bmp_header {
  uint16_t bf_type;
  uint32_t bfile_size;
  uint32_t bf_reserved;
  uint32_t b_offset_bits;
  uint32_t bi_size;
  uint32_t bi_width;
  uint32_t bi_height;
  uint16_t bi_planes;
  uint16_t bi_color_depth;
  uint32_t bi_compression;
  uint32_t bi_size_image;
  uint32_t bi_x_pels_per_meter;
  uint32_t bi_y_pels_per_meter;
  uint32_t bi_clr_used;
  uint32_t bi_clr_important;
};
#pragma pack(pop)

#endif

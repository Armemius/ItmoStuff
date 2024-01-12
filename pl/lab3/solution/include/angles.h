#include <stdio.h>
#include <stdlib.h>

#pragma once
#ifndef ANGLES_H
#define ANGLES_H

enum ANGLES {
	UNSUPPORTED_ANGLE = -1,
	ANGLE_0 = 0,
	ANGLE_90 = 90,
	ANGLE_180 = 180,
	ANGLE_270 = 270
};

enum ANGLES convert_angle(const int angle);

#endif

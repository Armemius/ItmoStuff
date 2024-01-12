#include "angles.h"

#include <stdio.h>
#include <stdlib.h>

enum ANGLES convert_angle(const int angle) {
	 switch (angle) {
	 case 0:
		 return ANGLE_0;
	 case 90:
		 return ANGLE_90;
	 case 180:
		 return ANGLE_180;
	 case 270:
		 return ANGLE_270;
	 default:
		 return UNSUPPORTED_ANGLE;
	 }
}

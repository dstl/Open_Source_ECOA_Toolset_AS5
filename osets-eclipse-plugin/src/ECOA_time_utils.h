/*
 * ECOA_time_utils.h
 */

#include "ECOA.h"


ECOA__return_status ECOA_setTimestamp(ECOA__timestamp *ts);

void ECOA_get_relative_local_time(ECOA__hr_time* relative_local_time);

ECOA__return_status ECOA_get_UTC_time(ECOA__global_time* utc_time);

ECOA__return_status ECOA_get_absolute_system_time(ECOA__global_time* absolute_system_time);

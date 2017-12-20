/*
 * ECOA_time_utils.h
 */

#include <time.h>

#include "ECOA_time_utils.h"

ECOA__return_status ECOA_setTimestamp(ECOA__timestamp *ts)
{
   struct timespec time;
   clock_gettime(CLOCK_REALTIME, &time);
   ts->seconds = time.tv_sec;
   ts->nanoseconds = time.tv_nsec;

   return ECOA__return_status_OK;
}

void ECOA_get_relative_local_time(ECOA__hr_time* relative_local_time)
{
   struct timespec time;
   clock_gettime(CLOCK_REALTIME, &time);
   relative_local_time->seconds = time.tv_sec;
   relative_local_time->nanoseconds = time.tv_nsec;
}

ECOA__return_status ECOA_get_UTC_time(ECOA__global_time* utc_time)
{
   // For now provide absolute system time...
   return ECOA_get_absolute_system_time(utc_time);
}

ECOA__return_status ECOA_get_absolute_system_time(ECOA__global_time* absolute_system_time)
{
   struct timespec time;
   clock_gettime(CLOCK_REALTIME, &time);
   absolute_system_time->seconds = time.tv_sec;
   absolute_system_time->nanoseconds = time.tv_nsec;

   return ECOA__return_status_OK;
}

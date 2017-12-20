/*
 * ECOA_time_utils.h
 */

#include <stdio.h>

#include "ECOA_time_utils.h"
#include "osl004_apos.h"
#include "osl004_apos_types.h"

ECOA__return_status ECOA_setTimestamp(ECOA__timestamp *ts)
{
   Time_Status_Type timeStatus;
   Time_Type aposTime;

   Get_Relative_Local_Time(&aposTime, &timeStatus);

   if (timeStatus == Time_OK)
   {
      ts->seconds = aposTime.Seconds;
      ts->nanoseconds = aposTime.Nanoseconds;
      return ECOA__return_status_OK;
   }
   else
   {
      /* Default the time */
      ts->seconds = 0;
      ts->nanoseconds = 0;
      return ECOA__return_status_RESOURCE_NOT_AVAILABLE;
   }
}

void ECOA_get_relative_local_time(ECOA__hr_time* relative_local_time)
{
   Time_Status_Type timeStatus;
   Time_Type aposTime;

   Get_Relative_Local_Time(&aposTime, &timeStatus);

   if (timeStatus == Time_OK)
   {
      relative_local_time->seconds = aposTime.Seconds;
      relative_local_time->nanoseconds = aposTime.Nanoseconds;
   }
   else
   {
      /* Default the time */
      relative_local_time->seconds = 0;
      relative_local_time->nanoseconds = 0;
      printf("Get relative time failed, %d\n", timeStatus);
   }
}

ECOA__return_status ECOA_get_UTC_time(ECOA__global_time* utc_time)
{
   // For now provide absolute system time...
   return ECOA_get_absolute_system_time(utc_time);
}

ECOA__return_status ECOA_get_absolute_system_time(ECOA__global_time* absolute_system_time)
{
   Time_Status_Type timeStatus;
   Time_Type aposTime;

   Get_Absolute_Local_Time(&aposTime, &timeStatus);

   if (timeStatus == Time_OK)
   {
      absolute_system_time->seconds = aposTime.Seconds;
      absolute_system_time->nanoseconds = aposTime.Nanoseconds;
      return ECOA__return_status_OK;
   }
   else
   {
      /* Default the time */
      absolute_system_time->seconds = 0;
      absolute_system_time->nanoseconds = 0;
      printf("Get absolute system time failed, %d\n", timeStatus);
      return ECOA__return_status_RESOURCE_NOT_AVAILABLE;
   }
}

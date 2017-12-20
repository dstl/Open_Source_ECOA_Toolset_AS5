#include <stdio.h>

#include <ecoaLog.h>
#include "posix_apos_binding.h"

void ecoaLog(unsigned char * logMessage, int messageLen, logLevel_t logLevel, int moduleInstanceID)
{

   SMNB_Status_Type SMNB_Status;

   // User code to log messages.
   switch (logLevel)
   {
   case LOG_LEVEL_CONTAINER_MONITOR:
      // Send message to monitoring panel. (use VC 255)
      Send_Message_Non_Blocking(255,
         logMessage,
         messageLen,
         &SMNB_Status);
      break;
   default :
      printf("%s", logMessage);
      fflush(stdout);
      break;
   }
}

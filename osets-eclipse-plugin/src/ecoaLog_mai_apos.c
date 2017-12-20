#include <stdio.h>

#include <ecoaLog.h>
#include "osl004_apos.h"
#include "osl004_apos_types.h"

void ecoaLog(unsigned char * logMessage, int messageLen, logLevel_t logLevel, int moduleInstanceID)
{

   SMNB_Status_Type SMNB_Status;

   // User code to log messages.
   switch (logLevel)
   {
   case LOG_LEVEL_CONTAINER_MONITOR:
      // Send message to monitoring panel.
      // Needs to match RTBP...
      //Send_Message_Non_Blocking(XXX,
      //   logMessage,
      //   messageLen,
      //   &SMNB_Status);
      break;
   default :
      printf("%s", logMessage);
      break;
   }
}

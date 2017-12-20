typedef enum
{
   LOG_LEVEL_TRACE,
   LOG_LEVEL_DEBUG,
   LOG_LEVEL_INFO,
   LOG_LEVEL_WARNING,
   LOG_LEVEL_ERROR,
   LOG_LEVEL_FATAL,
   LOG_LEVEL_CONTAINER_LEVEL_1,
   LOG_LEVEL_CONTAINER_LEVEL_2,
   LOG_LEVEL_CONTAINER_LEVEL_3,
   LOG_LEVEL_CONTAINER_MONITOR,
} logLevel_t;

void ecoaLog(unsigned char * logMessage, int messageLen, logLevel_t logLevel, int moduleInstanceID);

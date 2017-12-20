/* ILI Message definition */

#if !defined(_ILI_MESSAGE_H)
#define _ILI_MESSAGE_H


#include "ECOA.h"

/* Define the ILI message structure */
typedef struct {
   int messageID;
   ECOA__timestamp timestamp;
   ECOA__uint32 sourceID;
   ECOA__uint32 sequenceNumber;
   void * messageDataPointer;
} ILI_Message;

#endif  /* _ILI_MESSAGE_H */

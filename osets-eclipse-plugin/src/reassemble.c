/*
 * reassemble.c
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "ecoaByteswap.h"

// Define constants
#define BEGIN '\x00'
#define MIDDLE '\x10'
#define END '\x20'
#define BEGIN_END '\x30'

// Number of platforms
#define MAX_PLATFORMS 16
// Number of channels
#define MAX_CHANNELS 256

// Max size of UDP ELI payload
#define MAX_UDP_ELI_PAYLOAD 65503

// Size of the UDP message header
#if !defined( AMS_PHASE4 )
# define UDP_HDR_SIZE 4
#else
# define UDP_HDR_SIZE 2
#endif
// Size of the ELI message header
#define ELI_HDR_SIZE 24
// Position of the payload size in the ELI message header
#define PAYLOAD_SIZE_POS 16


// Struct to store a reassembled message
typedef struct ELI_payload
{
   unsigned char *message;
   unsigned int currentMessageSize;
   unsigned int expectedELISize;
} ELI_payload;

static ELI_payload messages[MAX_PLATFORMS][MAX_CHANNELS]; // Array of messages

// Function to reassemble message fragments one by one
// Sets the correct plid to point to the message
// Needs to be amended to use channel id to uniquely identify the message
void reassemble(unsigned char *fragment, unsigned int rxMessageSize, unsigned char **assembled_message, unsigned int *assembled_message_size)
{
   // Default the outputs.
   *assembled_message = NULL;
   *assembled_message_size = 0;

   int i;

   // Get the data from the UDP header.

   // Get UDP binding version
   unsigned char udpVersion = (*((unsigned char*) (fragment))) & '\xC0';
   // Get message part (begin, middle, end, begin+end)
   unsigned char messPart = (*((unsigned char*) (fragment))) & '\x30';

   // Get sender information:
   // Get sender platform id
   unsigned char plid = (*((unsigned char*) (fragment))) & '\x0f';
   unsigned int platform_id = (unsigned int) plid;
   // Get sender channel id
   unsigned char chid = *((unsigned char*) (fragment + 1));


   // Set ELI message content size (received size minus UDP header size)
   unsigned int eliContentSize = rxMessageSize - UDP_HDR_SIZE;
   // Set pointer to start of ELI message
   unsigned char *eliContentPtr = fragment + UDP_HDR_SIZE;

   // The length of the total ELI message
   unsigned int eliCompletePayloadSize;

#if !defined( AMS_PHASE4 )
   /* AMS Phase4 demo. messages don't include the messPart bits, as they don't do fragmentation.
    * The messages are therefore always BEGIN-END. */
   if ( messPart == BEGIN)
   {
      // Ensure there isn't already a message for the platform id
      if (messages[platform_id][chid].message != NULL)
      {
         // Discard message if there is one
         free(messages[platform_id][chid].message);
         printf("Reassemble Error - received a 2nd message from same sender without completing 1st!\n");
      }

      // Ensure it has a valid ELI header (0xECOA mark)
      unsigned short ecoaMark = bswap16(*((unsigned short *) eliContentPtr));
      if (ecoaMark == 0xEC0A)
      {
         // Get complete payload size
         eliCompletePayloadSize = bswap32(*((unsigned int *) (eliContentPtr + PAYLOAD_SIZE_POS)));


         // Ensure the content size received is the max size for a UDP fragment
         if (eliContentSize == MAX_UDP_ELI_PAYLOAD)
         {
            // Set the expected payload size
            messages[platform_id][chid].expectedELISize = eliCompletePayloadSize + ELI_HDR_SIZE;

            // Reset message size
            messages[platform_id][chid].currentMessageSize = 0;

            // Save message fragment
            unsigned char *message = (unsigned char *) malloc(eliCompletePayloadSize + ELI_HDR_SIZE);
            if (message == NULL)
            {
               exit(1);
               printf("Reassemble Error - malloc failed\n");
            }

            messages[platform_id][chid].message = message;
            memcpy(message, eliContentPtr, eliContentSize);
            // Adjust message size for fragment received
            messages[platform_id][chid].currentMessageSize = messages[platform_id][chid].currentMessageSize + eliContentSize;

            // Set the outputs.
            *assembled_message = NULL;
            *assembled_message_size = 0;
            }
         else
         {
            printf("Reassemble Error - received a BEGIN message not of full size (65503)\n");
         }
      }
      else
      {
         printf("Reassemble Error - incorrect message format (no ECOA mark in ELI header)\n");
      }
   }
   else if (messPart == MIDDLE)
   {
      if (messages[platform_id][chid].message != NULL)
      {
         // Ensure the content size received is the max size for a UDP fragment
         if (eliContentSize == MAX_UDP_ELI_PAYLOAD)
         {
            // Copy message fragment
            memcpy(messages[platform_id][chid].message + messages[platform_id][chid].currentMessageSize, eliContentPtr, eliContentSize);
            // Adjust message size for fragment size
            messages[platform_id][chid].currentMessageSize = messages[platform_id][chid].currentMessageSize + eliContentSize;
         }
         else
         {
            printf("Reassemble Error - received a MIDDLE message not of full size (65503)\n");
         }
      }
      else
      {
         printf("Reassemble Error - MIDDLE received before BEGIN\n");
      }

      // Set the outputs.
      *assembled_message = NULL;
      *assembled_message_size = 0;
   }
   else if (messPart == END)
   {
      if (messages[platform_id][chid].message != NULL)
      {
         // Copy message fragment
         memcpy(messages[platform_id][chid].message + messages[platform_id][chid].currentMessageSize, eliContentPtr, eliContentSize);
         // Adjust message size for fragment size
         messages[platform_id][chid].currentMessageSize = messages[platform_id][chid].currentMessageSize + eliContentSize;

         // Ensure the total assembled size matches the expected size.
         if (messages[platform_id][chid].currentMessageSize == messages[platform_id][chid].expectedELISize)
         {
            // Set the outputs.
            *assembled_message = messages[platform_id][chid].message;
            *assembled_message_size = messages[platform_id][chid].currentMessageSize;
         }
         else
         {
            printf("Reassemble Error - Total assembled size does not match expected payload size\n");

            // Default the outputs.
            *assembled_message = NULL;
            *assembled_message_size = 0;
         }
      }
      else
      {
         printf("Reassemble Error - END received before BEGIN\n");

         // Default the outputs.
         *assembled_message = NULL;
         *assembled_message_size = 0;
      }

      // Reset message pointer and size
      messages[platform_id][chid].message = NULL;
      messages[platform_id][chid].currentMessageSize = 0;
   }
   else
#endif /* AMS_PHASE4 */
   {
      // This is a BEGIN-END

      // Check if there is already a message from the platform
      if (messages[platform_id][chid].message != NULL)
      {
         // Discard message if there is one
         free(messages[platform_id][chid].message);
         printf("Reassemble Error - received a 2nd message from same sender without completing 1st!\n");
      }

      // Ensure it has a valid ELI header (0xECOA mark)
      unsigned short ecoaMark = bswap16(*((unsigned short *) eliContentPtr));
      if (ecoaMark == 0xEC0A)
      {
         // Set complete payload size - should be less than 65503!
         eliCompletePayloadSize = bswap32(*((unsigned int *) (eliContentPtr + PAYLOAD_SIZE_POS)));

         if (eliCompletePayloadSize < MAX_UDP_ELI_PAYLOAD)
         {
            // Ensure the size received matches the size expected
            if (eliCompletePayloadSize == (eliContentSize - ELI_HDR_SIZE))
            {
               // Reset message size
               messages[platform_id][0].currentMessageSize = 0;

               // Copy message to correct buffer
               unsigned char *message = (unsigned char *) malloc(eliCompletePayloadSize + ELI_HDR_SIZE);
               if (message == NULL)
               {
                  exit(1);
                  printf("Reassemble Error - malloc failed\n");
               }

               memcpy(message, eliContentPtr, eliContentSize);
               messages[platform_id][chid].message = message;
               // Set message size
               messages[platform_id][chid].currentMessageSize = eliContentSize;

               // Set the outputs.
               *assembled_message = messages[platform_id][chid].message;
               *assembled_message_size = messages[platform_id][chid].currentMessageSize;

               // Reset message size
               messages[platform_id][chid].message = NULL;
               messages[platform_id][chid].currentMessageSize = 0;
            }
            else
            {
               printf("Reassemble Error - message size received is not the same as ELI Header payload size\n");
            }
         }
         else
         {
            printf("Reassemble Error - received a BEGIN-END message with payload size > 65503\n");
         }
      }
      else
      {
         printf("Reassemble Error - incorrect message format (no ECOA mark in ELI header)\n");
      }
   }
}

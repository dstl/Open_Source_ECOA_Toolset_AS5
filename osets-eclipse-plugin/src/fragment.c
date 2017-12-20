/*
 ============================================================================
 Name        : fragment.c
 Author      : Neil Henderson
 Version     : v1
 Copyright   : BAE Systems MAI
 Description : Program to split an ELI message into fragments for transmission
 	 	 	   by UDP.
 ============================================================================
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>
//#include <netinet/in.h> // for htonl and ntohl - not available

#include "ecoaByteswap.h"
#include "fragment.h"

// Define constants
static const int PAYLOAD_SIZE = 65503;					// Maximum payload size
static const int FRAGMENT_SIZE = 65507;					// Maximum size of message fragment

#define BEGIN '\x00'
#define MIDDLE '\x10'
#define END '\x20'
#define BEGIN_END '\x30'

#if !defined( AMS_PHASE4 )
# define UDP_HDR_SIZE 4
#else
# define UDP_HDR_SIZE 2
#endif

void increment_counter(unsigned short *counter)
{
	if(*counter == 65535)
		*counter = 0;
	else
		*counter = *counter + 1;
}


// Function to split the message into fragments
// adds the message generic content to the message and sets the message part e.g. BEGIN or MIDDLE
// Parameters:
// message: message to be fragmented
// size:	size of message to be fragmented in bytes
// fragments: array of pointers to fragments of message
// number of fragments after message split
unsigned int fragment(unsigned char plid, unsigned char chanid, unsigned short *counter, unsigned char *message, unsigned int size, fragmentObj_t *fragmentObjs)
{
	int i;
	unsigned int numFrags = 0;
	unsigned short count;
	if(size<=PAYLOAD_SIZE)
	{
		unsigned char *fragment = (unsigned char *)malloc(size + UDP_HDR_SIZE);
		// add platform id and channel id to first and second byte of message
		// add fragment bits to first byte
#if !defined( AMS_PHASE4 )
		fragment[0]=plid | BEGIN_END;
#else
		/* AMS Phase4 demo. messages don't include the messPart bits, as they don't do fragmentation.
		 * The must always be zero (0b00) AS IF the packet is a BEGIN packet.... */
		fragment[0]=plid | BEGIN;
#endif
		fragment[1]=chanid;
		// copy message into buffer (allow for header size)
#if !defined( AMS_PHASE4 )
		// add channel counter to header
		count = bswap16(*counter);
		memcpy(fragment+2, &count, 2);
		increment_counter(counter);
#endif
		memcpy((fragment+UDP_HDR_SIZE), message, size);
		// save fragment and set number of fragments
		fragmentObjs[0].fragment = fragment;
		fragmentObjs[0].sizeOfFragment = size + UDP_HDR_SIZE;
		numFrags = 1;
		return numFrags;
	}
#if !defined( AMS_PHASE4 )
	else
	{
		while(size > PAYLOAD_SIZE)
		{
			unsigned char *fragment = (unsigned char *)malloc(FRAGMENT_SIZE);

			if(fragment==NULL) exit(1);

			// add platform id to fist byte of buffer
			if(numFrags == 0)
			{
				fragment[0]=plid | BEGIN;
			}
			else
			{
				fragment[0]=plid | MIDDLE;
			}
			fragment[1]=chanid;
			// add channel counter to header
			count = bswap16(*counter);
			memcpy(fragment+2, &count, 2);
			increment_counter(counter);
			// copy and save message fragment (allow for header size)
			memcpy(fragment+4, message, PAYLOAD_SIZE);
			fragmentObjs[numFrags].fragment = fragment;
		    fragmentObjs[numFrags].sizeOfFragment = FRAGMENT_SIZE;
			// adjust pointer for message fragment size
			message = message + PAYLOAD_SIZE;
			// increment number of fragments
			numFrags = numFrags + 1;
			// reduce size of message left to fragment
			size = size - PAYLOAD_SIZE;
		}
		unsigned char *fragment = (unsigned char *)malloc(size + UDP_HDR_SIZE);
		// add platform id and channel id to first and second byte of message
		fragment[0]=plid | END;
		fragment[1]=chanid;
		// add channel counter to header
		count = bswap16(*counter);
		memcpy(fragment+2, &count, 2);
		increment_counter(counter);
		// copy and save fragment (allow for header size)
		memcpy(fragment+4, message, size);
		fragmentObjs[numFrags].fragment = fragment;
		fragmentObjs[numFrags].sizeOfFragment = size + UDP_HDR_SIZE;
		// increment number of fragments
		numFrags = numFrags + 1;
		return numFrags;
	}
#endif
}


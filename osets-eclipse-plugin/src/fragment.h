/*
 * fragment.h
 *
 *  Created on: Jul 10, 2013
 *      Author: neil.henderson2
 */

#ifndef FRAGMENT_H_
#define FRAGMENT_H_


typedef struct
{
   unsigned char *fragment;
   unsigned int sizeOfFragment;
} fragmentObj_t;

//unsigned int fragment(unsigned char plid, unsigned int chanid, unsigned char *message, unsigned int size, unsigned char *fragments[]);
unsigned int fragment(unsigned char plid, unsigned char chanid, unsigned short *counter, unsigned char *message, unsigned int size, fragmentObj_t *fragmentObjs);

#endif /* FRAGMENT_H_ */

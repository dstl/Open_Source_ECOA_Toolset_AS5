/*
 * prctl.h
 * 
 * Linux offers a means of naming and modifying threads and processes not available in POSIX.
 */

#ifndef _PRCTL_H_
#define _PRCTL_H_

#if defined(__linux__)

#include <sys/prctl.h>

#endif /* __linux__ */

#endif /* _PRCTL_H_ */
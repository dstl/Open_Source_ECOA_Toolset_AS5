/*
 * ECOA_file_handler.h
 */

#include "ECOA.h"
#include <stdio.h>

ECOA__return_status ECOA_write_zeroed_context(char *filename, unsigned int contextSize);

ECOA__return_status ECOA_write_context(char *filename, void *warm_context, unsigned int contextSize);

ECOA__return_status ECOA_get_context(char *filename, void *warm_context, unsigned int contextSize);

ECOA__return_status ECOA_read_file(FILE *file, ECOA__byte *memory_address, ECOA__uint32 in_size, ECOA__uint32 *out_size, ECOA__uint32 *index);

ECOA__return_status ECOA_seek_file(FILE *file, ECOA__int32 offset, ECOA__seek_whence_type whence, ECOA__uint32 *index, ECOA__uint32 *current_file_size);

ECOA__return_status ECOA_write_file(FILE *file, ECOA__byte *memory_address, ECOA__uint32 in_size, ECOA__uint32 *index, ECOA__uint32 max_capacity);

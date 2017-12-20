/*
 * ECOA_file_handler.c
 */

#include <string.h>

#include "ECOA_file_handler.h"
#include "ECOA.h"

ECOA__return_status ECOA_write_zeroed_context(char *filename, unsigned int contextSize)
{
   // Crete a zeroed buffer.
   char buffer[contextSize];
   memset(buffer, 0, contextSize);

   FILE *fp = fopen(filename,"wb");

   int size = fwrite(buffer, 1, contextSize, fp);

   fclose(fp);

   if (size > 0)
   {
      return ECOA__return_status_OK;
   }
   else
   {
      return ECOA__return_status_INVALID_HANDLE;
   }
}

ECOA__return_status ECOA_write_context(char *filename, void *warm_context, unsigned int contextSize)
{
   FILE *fp = fopen(filename,"wb");

   int size = fwrite(warm_context, 1, contextSize, fp);

   fclose(fp);

   if (size > 0)
   {
      return ECOA__return_status_OK;
   }
   else
   {
      return ECOA__return_status_INVALID_HANDLE;
   }
}

ECOA__return_status ECOA_get_context(char *filename, void *warm_context, unsigned int contextSize)
{
   FILE *fp = fopen(filename,"rb");
   int status = -1;

   if (fp != NULL)
   {
      status = fread(warm_context, 1, contextSize, fp);

      fclose(fp);
   }

   if (status > 0)
   {
      return ECOA__return_status_OK;
   }
   else
   {
      return ECOA__return_status_INVALID_HANDLE;
   }
}

ECOA__return_status ECOA_read_file(FILE *file, ECOA__byte *memory_address, ECOA__uint32 in_size, ECOA__uint32 *out_size, ECOA__uint32 *index)
{
   ECOA__return_status returnStatus;
   *out_size = 0;

   if (file != NULL)
   {
      *out_size = fread(memory_address, 1, in_size, file);
   }

   if (*out_size > 0)
   {
      *index += in_size;
      returnStatus = ECOA__return_status_OK;
   }
   else
   {
      returnStatus = ECOA__return_status_RESOURCE_NOT_AVAILABLE;
   }

   return returnStatus;
}

ECOA__return_status ECOA_seek_file(FILE *file, ECOA__int32 offset, ECOA__seek_whence_type whence, ECOA__uint32 *index, ECOA__uint32 *current_file_size)
{
   ECOA__return_status returnStatus;
   int status = -1;

   if (file != NULL)
   {
      int origin;
      switch (whence)
      {
         case ECOA__seek_whence_type_SEEK_SET:
         {
            origin = SEEK_SET;
            if (offset < 0 || offset > (*current_file_size))
            {
               returnStatus =  ECOA__return_status_INVALID_PARAMETER;
            }
            else
            {
               status = fseek(file, offset, origin);
               if (status == 0)
               {
                  *index = 0;
                  returnStatus =  ECOA__return_status_OK;
               }
               else
               {
                  returnStatus = ECOA__return_status_RESOURCE_NOT_AVAILABLE;
               }
            }
            break;
         }
         case ECOA__seek_whence_type_SEEK_CUR:
         {
            origin = SEEK_CUR;
            if ((offset + (*index)) < 0 || (offset + (*index)) > (*current_file_size))
            {
               returnStatus =  ECOA__return_status_INVALID_PARAMETER;
            }
            else
            {
               status = fseek(file, offset, origin);
               if (status == 0)
               {
                  *index += offset;
                  returnStatus =  ECOA__return_status_OK;
               }
               else
               {
                  returnStatus = ECOA__return_status_RESOURCE_NOT_AVAILABLE;
               }
            }
            break;
         }
         case ECOA__seek_whence_type_SEEK_END:
         {
            origin = SEEK_END;
            if (((*current_file_size) + offset) < 0 || ((*current_file_size) + offset) > (*current_file_size))
            {
               returnStatus =  ECOA__return_status_INVALID_PARAMETER;
            }
            else
            {
               status = fseek(file, offset, origin);
               if (status == 0)
               {
                  *index = (*current_file_size) + offset;
                  returnStatus =  ECOA__return_status_OK;
               }
               else
               {
                  returnStatus = ECOA__return_status_RESOURCE_NOT_AVAILABLE;
               }
            }
            break;
         }
      }
   }

   return returnStatus;

}

ECOA__return_status ECOA_write_file(FILE *file, ECOA__byte *memory_address, ECOA__uint32 in_size, ECOA__uint32 *index, ECOA__uint32 max_capacity)
{
   ECOA__return_status returnStatus;

   int status;

   if (file != NULL)
   {
      if (((*index) + in_size) <= max_capacity)
      {
         status = fwrite(memory_address, 1, in_size, file);

         if (status == in_size)
         {
            fflush(file);
            *index += in_size;
            returnStatus = ECOA__return_status_OK;
         }
         else
         {
            returnStatus = ECOA__return_status_RESOURCE_NOT_AVAILABLE;
         }
      }
      else
      {
         returnStatus = ECOA__return_status_INVALID_PARAMETER;
      }
   }

   return returnStatus;
}

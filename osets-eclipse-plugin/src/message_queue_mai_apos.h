/*
 * @file "message_queue.h"
 * This is the message queue interface header
 */

#if !defined(_MESSAGE_QUEUE_H)
#define _MESSAGE_QUEUE_H

#if defined(__cplusplus)
extern "C" {
#endif /* __cplusplus */

typedef void* Message_Queue_ID_Type;
  // void* because a pointer is used as the identifier...
  // on 32-bit == unsigned int/long
  // on 64-bit != unsigned int/long

typedef enum {
  Create_Message_Queue_OK,
  Create_Message_Queue_Failed
} Create_Message_Queue_Status_Type;

typedef enum {
  Delete_Message_Queue_OK,
  Delete_Message_Queue_Failed
} Delete_Message_Queue_Status_Type;

typedef enum {
  Send_Message_Queue_OK,
  Send_Message_Queue_Failed
} Send_Message_Queue_Status_Type;

typedef enum {
  Receive_Message_Queue_OK,
  Receive_Message_Queue_Failed
} Receive_Message_Queue_Status_Type;

extern void Create_Message_Queue(int Queue_Length,
		                         int Message_Length,
		                         Message_Queue_ID_Type *QID,
		                         Create_Message_Queue_Status_Type *Create_Message_Queue_Status);

extern void Delete_Message_Queue(Message_Queue_ID_Type QID,
                                 Delete_Message_Queue_Status_Type *Delete_Message_Queue_Status);

extern void Send_Message_Queue(Message_Queue_ID_Type QID,
                               void *Message_Address,
                               int Message_Length,
                               Send_Message_Queue_Status_Type *Send_Message_Queue_Status);

extern void Receive_Message_Queue(Message_Queue_ID_Type QID,
                                  void *Buffer_Address,
                                  int Buffer_Length,
                                  Receive_Message_Queue_Status_Type *Receive_Message_Queue_Status);

extern int Get_Queue_Size(Message_Queue_ID_Type QID);

#if defined(__cplusplus)
}
#endif /* __cplusplus */

#endif  /* _MESSAGE_QUEUE_H */

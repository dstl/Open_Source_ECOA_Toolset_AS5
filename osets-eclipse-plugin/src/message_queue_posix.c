/*
 * @file "message_queue.c"
 * This is the message queue implementation
 */

#include <stdlib.h>
#include <string.h>
#include <stdio.h>
#include <unistd.h> /* for ssize_t */
#include "message_queue.h"
#include "posix_apos_binding.h"

typedef struct {
	int Count_Semaphore;
	int Access_Semaphore;
	int Count;
	int Queue_Length;
	int Message_Length;
	unsigned char *Read_Pointer;
	unsigned char *Write_Pointer;
	unsigned char *Message_Store;
} Message_Queue_Type;

void Create_Message_Queue(int Queue_Length,
		int Message_Length,
		Message_Queue_ID_Type *QID,
		Create_Message_Queue_Status_Type *Create_Message_Queue_Status)
{
	Create_Semaphore_Status_Type Create_Semaphore_Status;

	Message_Queue_Type *qPtr;
	unsigned char *qData;

	/* Assume failure */
	*QID = 0;
	*Create_Message_Queue_Status = Create_Message_Queue_Failed;

	qPtr = (Message_Queue_Type *) malloc (sizeof(Message_Queue_Type));

	if (qPtr > 0)
	{

		qData = (unsigned char *) malloc (Queue_Length * Message_Length);

		if (qData > 0)
		{

			Create_Semaphore(0,
					Queue_Length,
					Queuing_Discipline_FIFO,
					&qPtr->Count_Semaphore,
					&Create_Semaphore_Status);

			if (Create_Semaphore_Status == Create_Semaphore_OK) {

				Create_Semaphore(1,
						1,
						Queuing_Discipline_FIFO,
						&qPtr->Access_Semaphore,
						&Create_Semaphore_Status);

				if (Create_Semaphore_Status == Create_Semaphore_OK) {

					qPtr->Count = 0;
					qPtr->Queue_Length = Queue_Length;
					qPtr->Message_Length = Message_Length;
					qPtr->Read_Pointer = qData;
					qPtr->Write_Pointer = qData;
					qPtr->Message_Store = qData;

					*QID = (Message_Queue_ID_Type)qPtr;
					*Create_Message_Queue_Status = Create_Message_Queue_OK;

				} else
				{
					printf("ERROR access semaphore\n");
				}
			} else
			{
				printf("ERROR count semaphore - %d\n", Create_Semaphore_Status);
			}
		} else
		{
			printf("ERROR qData <= 0\n");
		}
	} else
	{
		printf("ERROR qPtr <= 0\n");
	}
}

void Delete_Message_Queue(Message_Queue_ID_Type QID,
		Delete_Message_Queue_Status_Type *Delete_Message_Queue_Status)
{
	Message_Queue_Type *qPtr = (Message_Queue_Type *)QID;

	Delete_Semaphore_Status_Type Delete_Semaphore_Status;

	/* Assume failure */
	*Delete_Message_Queue_Status = Delete_Message_Queue_Failed;

	Delete_Semaphore(qPtr->Count_Semaphore,
			&Delete_Semaphore_Status);

	if (Delete_Semaphore_Status == Delete_Semaphore_OK) {

		Delete_Semaphore(qPtr->Access_Semaphore,
				&Delete_Semaphore_Status);

		if (Delete_Semaphore_Status == Delete_Semaphore_OK) {

			free(qPtr->Message_Store);
			free(qPtr);

			*Delete_Message_Queue_Status = Delete_Message_Queue_OK;
		}

	} else

		*Delete_Message_Queue_Status = Delete_Message_Queue_OK;
}

void Send_Message_Queue(Message_Queue_ID_Type QID,
		void *Message_Address,
		int Message_Length,
		Send_Message_Queue_Status_Type *Send_Message_Queue_Status)
{
	Message_Queue_Type *qPtr = (Message_Queue_Type *)QID;
	Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;
	Post_Semaphore_Status_Type Post_Semaphore_Status;
	Time_Type Timeout={MAX_SECONDS,MAX_NANOSECONDS};

	/* Assume failure */
	*Send_Message_Queue_Status = Send_Message_Queue_Failed;

	Wait_For_Semaphore(qPtr->Access_Semaphore,
			&Timeout,
			&Wait_For_Semaphore_Status);

	if(Wait_For_Semaphore_Status == Wait_For_Semaphore_OK) {

		if (qPtr->Count < qPtr->Queue_Length) {

			memcpy(qPtr->Write_Pointer,
					Message_Address,
					Message_Length);

			if (qPtr->Write_Pointer >= (unsigned char*)((ssize_t)qPtr->Message_Store + ((qPtr->Queue_Length - 1) * qPtr->Message_Length)))
				qPtr->Write_Pointer = qPtr->Message_Store;
			else
				qPtr->Write_Pointer += qPtr->Message_Length;

			qPtr->Count++;

			Post_Semaphore(qPtr->Count_Semaphore,
					&Post_Semaphore_Status);

			if (Post_Semaphore_Status == Post_Semaphore_OK) {

				if (Post_Semaphore_Status == Post_Semaphore_OK)

					*Send_Message_Queue_Status = Send_Message_Queue_OK;

			}

		}

		Post_Semaphore(qPtr->Access_Semaphore,
				&Post_Semaphore_Status);

	} else
	{
		printf("SMSGQ Wait for access semaphore failed - %d\n", Wait_For_Semaphore_Status);
	}
}

void Receive_Message_Queue(Message_Queue_ID_Type QID,
		void *Buffer_Address,
		int Buffer_Length,
		Receive_Message_Queue_Status_Type *Receive_Message_Queue_Status)
{
	Message_Queue_Type *qPtr = (Message_Queue_Type *)QID;
	Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;
	Post_Semaphore_Status_Type Post_Semaphore_Status;
	Time_Type Timeout={MAX_SECONDS,MAX_NANOSECONDS};

	/* Assume failure */
	*Receive_Message_Queue_Status = Receive_Message_Queue_Failed;

	Wait_For_Semaphore(qPtr->Count_Semaphore,
			&Timeout,
			&Wait_For_Semaphore_Status);

	if(Wait_For_Semaphore_Status == Wait_For_Semaphore_OK) {

		Wait_For_Semaphore(qPtr->Access_Semaphore,
				&Timeout,
				&Wait_For_Semaphore_Status);

		if(Wait_For_Semaphore_Status == Wait_For_Semaphore_OK) {

			memcpy(Buffer_Address,
					qPtr->Read_Pointer,
					Buffer_Length);

			if (qPtr->Read_Pointer >= (unsigned char*)((ssize_t)qPtr->Message_Store + ((qPtr->Queue_Length - 1) * qPtr->Message_Length)))

				qPtr->Read_Pointer = qPtr->Message_Store;
			else
				qPtr->Read_Pointer += qPtr->Message_Length;

			qPtr->Count--;

			Post_Semaphore(qPtr->Access_Semaphore,
					&Post_Semaphore_Status);

			if (Post_Semaphore_Status == Post_Semaphore_OK)

				*Receive_Message_Queue_Status = Receive_Message_Queue_OK;

		}else
		{
			printf("RMSGQ Wait for access semaphore failed - %d\n", Wait_For_Semaphore_Status);
		}
	} else
	{
		printf("RMSGQ Wait for count semaphore failed - %d\n", Wait_For_Semaphore_Status);
	}
}

int Get_Queue_Size(Message_Queue_ID_Type QID)
{
   Message_Queue_Type *qPtr = (Message_Queue_Type *)QID;
   Wait_For_Semaphore_Status_Type Wait_For_Semaphore_Status;
   Post_Semaphore_Status_Type Post_Semaphore_Status;
   Time_Type Timeout={MAX_SECONDS,MAX_NANOSECONDS};
   int currentSize = 0;

   Wait_For_Semaphore(qPtr->Access_Semaphore,
         &Timeout,
         &Wait_For_Semaphore_Status);

   if(Wait_For_Semaphore_Status == Wait_For_Semaphore_OK)
   {
      currentSize =  qPtr->Count;

      Post_Semaphore(qPtr->Access_Semaphore,
            &Post_Semaphore_Status);
   } else
   {
      printf("Get_Queue_Size Wait for access semaphore failed - %d\n", Wait_For_Semaphore_Status);
   }

   return currentSize;
}

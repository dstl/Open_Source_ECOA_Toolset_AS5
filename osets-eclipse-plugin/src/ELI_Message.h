/* ELI Message definition */

#ifndef ELI_HEADER_H_
#define ELI_HEADER_H_

#include "ECOA.h"

typedef ECOA__int32 ELI_Message__Domain;
#define ELI_Message__Domain_PLATFORM (0)
#define ELI_Message__Domain_SERVICE_OP (1)
#define ELI_Message__Domain_PROTECTION_DOMAIN (2)

typedef ECOA__int32 ELI_Message__PlatformMessageID;
#define ELI_Message__PlatformMessageID_PLATFORM_STATUS (1)
#define ELI_Message__PlatformMessageID_PLATFORM_STATUS_REQUEST (2)
#define ELI_Message__PlatformMessageID_AVAILABILITY_STATUS (3)
#define ELI_Message__PlatformMessageID_AVAILABILITY_STATUS_REQUEST (4)
#define ELI_Message__PlatformMessageID_UNKNOWN_OPERATION (5)
#define ELI_Message__PlatformMessageID_SERVICE_NOT_AVAILABLE (6)
#define ELI_Message__PlatformMessageID_VERSIONED_DATA_PULL (7)
#define ELI_Message__PlatformMessageID_COMPOSITE_CHANGE_REQUEST (8)
#define ELI_Message__PlatformMessageID_COMPOSITE_CHANGE_REQUEST_ACK (9)


typedef struct {
   ECOA__uint16 ecoaMark;
   ECOA__char8 version_domain;
   ECOA__uint8 logicalPlatform;
   ECOA__uint32 ID;
   ECOA__uint32 seconds;
   ECOA__uint32 nanoseconds;
   ECOA__uint32 payloadSize;
   ECOA__uint32 sequenceNumber;
} ELIHeader;

typedef struct {
      ECOA__uint32 serviceID;
      ECOA__uint32 availabilityState;
} ServiceAvailability;

typedef ECOA__uint32 ELI_Message__PlatformStatus;
#define ELI_Message__PlatformStatus_DOWN (0)
#define ELI_Message__PlatformStatus_UP (1)


typedef struct {
   ELI_Message__PlatformStatus status;
   ECOA__uint32 compositeID;
} PlatformStatus;
typedef struct {
   ECOA__uint32 providedServices;
   /* variable array of services/availability here */
} AvailabilityStatus;
typedef struct {
   ECOA__uint32 serviceID;
} AvailabilityStatusRequest;
typedef struct {
   ECOA__uint32 compositeID;
} CompositeChangeRequest;
typedef struct {
   ECOA__uint32 status;
} CompositeChangeRequestAck;
typedef struct {
   ECOA__uint32 euid;
} VersionedDataPull;
typedef struct {
   ECOA__uint32 euid;
} UnknownOperation;


typedef union
{
   PlatformStatus platformStatus;
   AvailabilityStatus availabilityStatus;
   /* No params for PlatformStatusRequest */
   AvailabilityStatusRequest availabilityStatusRequest;
   CompositeChangeRequest compositeChangeRequest;
   CompositeChangeRequestAck compositeChangeRequestAck;
   VersionedDataPull versionedDataPull;
   UnknownOperation unknownOperation;
} PlatformELIPayload;

#endif /* ELI_HEADER_H_ */

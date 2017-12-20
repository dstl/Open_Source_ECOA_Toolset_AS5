/*
 * @file ECOA.hpp
 */

/*  This is a compilable ISO C++ 98 specification of the generic ECOA       */
/*  types derived from the C++ binding specification.                       */

/*  The declarations of the types given below are taken from the            */
/*  standard, as are the enum types and the names of the others types.      */
/*  Unless specified as implementation dependent, the values specified in   */
/*  this appendix should be implemented as defined.                         */


#if !defined(__ECOA_HPP__)
#define __ECOA_HPP__

namespace ECOA {

  /* ECOA:boolean8 */
  typedef unsigned char boolean8;
  static const boolean8 TRUE =          1;
  static const boolean8 FALSE =         0;

  /* ECOA:int8 */
  typedef char int8;
  static const int8 INT8_MIN =    -127;
  static const int8 INT8_MAX =     127;

  /* ECOA:char8 */
  typedef char char8;
  static const char8 CHAR8_MIN =    0;
  static const char8 CHAR8_MAX =    127;

  /* ECOA:byte */
  typedef unsigned char byte;
  static const byte BYTE_MIN =      0;
  static const byte BYTE_MAX =      255;

  /* ECOA:int16 */
  typedef short int int16;
  static const int16 INT16_MIN =    -32767;
  static const int16 INT16_MAX =     32767;

  /* ECOA:int32 */
  typedef int int32;
  static const int32 INT32_MIN =     -2147483647L;
  static const int32 INT32_MAX =      2147483647L;

    /* ECOA:uint8 */
  typedef unsigned char uint8;
  static const uint8 UINT8_MIN =      0;
  static const uint8 UINT8_MAX =      255;

  /* ECOA:uint16 */
  typedef unsigned short int uint16;
  static const uint16 UINT16_MIN =     0;
  static const uint16 UINT16_MAX =     65535;

  /* ECOA:uint32 */
  typedef unsigned int uint32;
  static const uint32 UINT32_MIN =    0LU;
  static const uint32 UINT32_MAX =    4294967295LU;

#if defined (ECOA_64BIT_SUPPORT)
  /* ECOA:int64 */
  typedef long long int int64;
  static const int64 INT64_MIN =     -9223372036854775807LL;
  static const int64 INT64_MAX =      9223372036854775807LL;

  /* ECOA:uint64 */
  typedef unsigned long long int uint64;
  static const uint64 UINT64_MIN =    0LLU;
  static const uint64 UINT64_MAX =    18446744073709551615LLU;
#endif /* ECOA_64BIT_SUPPORT */

  /* ECOA:float32 */
  typedef float float32;
  static const float32 FLOAT32_MIN =  -3.402823466e+38F;
  static const float32 FLOAT32_MAX =   3.402823466e+38F;

/* ECOA:double64 */
  typedef double double64;
  static const double64 DOUBLE64_MIN = -1.7976931348623157e+308; /* NO suffix == double (64-bit) */
  static const double64 DOUBLE64_MAX =  1.7976931348623157e+308; /* SOME compilers accept "D" suffix, but this is non standard. */

  /* ECOA:return_status */
  struct return_status {
    ECOA::uint32 value;
    enum EnumValues {
      OK =                       0,
      INVALID_HANDLE =            1,
      DATA_NOT_INITIALIZED =      2,
      NO_DATA =                   3,
      INVALID_IDENTIFIER =        4,
      NO_RESPONSE =               5,
      OPERATION_ALREADY_PENDING = 6,
      INVALID_SERVICE_ID =        7,
      CLOCK_UNSYNCHRONIZED =      8,
      INVALID_TRANSITION =        9,
      RESOURCE_NOT_AVAILABLE =    10,
      OPERATION_NOT_AVAILABLE =   11,
      PENDING_STATE_TRANSITION =  12,
      INVALID_PARAMETER =         13

    };
    inline void operator = (ECOA::uint32 i) { value = i; }
    inline operator ECOA::uint32 () const { return value; }
  };

  /* ECOA:hr_time */
  typedef struct {
    ECOA::uint32 seconds; 			/* Seconds */
    ECOA::uint32 nanoseconds;   		/* Nanoseconds*/
  } hr_time;

  /* ECOA:global_time */
  typedef struct {
    ECOA::uint32 seconds; 			/* Seconds */
    ECOA::uint32 nanoseconds;   		/* Nanoseconds*/
  } global_time;

  /* ECOA:duration */
  typedef struct {
    ECOA::uint32 seconds;                       /* Seconds */
    ECOA::uint32 nanoseconds;                   /* Nanoseconds*/
  } duration;

  /* ECOA:timestamp */
  typedef struct {
    ECOA::uint32 seconds;                       /* Seconds */
    ECOA::uint32 nanoseconds;                   /* Nanoseconds*/
  } timestamp;

  /* ECOA:log */
  static const ECOA::uint32 LOG_MAXSIZE = 256;
  typedef struct {
    ECOA::uint32 current_size;
    ECOA::char8  data[LOG_MAXSIZE];
  } log;

  /* ECOA:module_states_type */
  struct module_states_type {
    uint32 value;
    enum EnumValues {
      IDLE = 0,
      READY = 1,
      RUNNING = 2
    };
    inline void operator = (uint32 i) { value = i; }
    inline operator uint32() const { return value; }
  };

  /* ECOA:module_error_type */
  struct module_error_type {
    ECOA::uint32 value;
    enum EnumValues {
      ERROR = 0,
      FATAL_ERROR = 1
    };
    inline void operator = (ECOA::uint32 i) { value = i; }
    inline operator ECOA::uint32() const { return value; }
  };

  /* ECOA:error_id */
  typedef ECOA::uint32 error_id;

  /* ECOA:asset_id */
  typedef ECOA::uint32 asset_id;

  /* ECOA:asset_id */
  struct asset_type {
    ECOA::uint32 value;
    enum EnumValues {
      COMPONENT = 0,
      PROTECTION_DOMAIN = 1,
      NODE = 2,
      PLATFORM = 3,
      SERVICE = 4,
      DEPLOYMENT = 5
    };
    inline void operator = (ECOA::uint32 i) { value = i; }
    inline operator ECOA::uint32() const { return value; }
  };

  /* ECOA:error_type */
  struct error_type {
    uint32 value;
    enum EnumValues {
      RESOURCE_NOT_AVAILABLE = 0,
      UNAVAILABLE = 1,
            MEMORY_VIOLATION = 2,
            NUMERICAL_ERROR = 3,
            ILLEGAL_INSTRUCTION = 4,
            STACK_OVERFLOW = 5,
            DEADLINE_VIOLATION = 6,
            OVERFLOW = 7,
            UNDERFLOW = 8,
            ILLEGAL_INPUT_ARGS = 9,
            ILLEGAL_OUTPUT_ARGS = 10,
            ERROR = 11,
            FATAL_ERROR = 12,
            HARDWARE_FAULT = 13,
            POWER_FAIL = 14,
            COMMUNICATION_ERROR = 15,
            INVALID_CONFIG = 16,
            INITIALISATION_PROBLEM = 17,
            CLOCK_UNSYNCHRONIZED = 18,
            UNKNOWN_OPERATION = 19,
            OPERATION_OVERRATED = 20,
            OPERATION_UNDERRATED = 21
    };
    inline void operator = (uint32 i) { value = i; }
    inline operator uint32() const { return value; }
  };

  /* ECOA:recovery_action_type */
  struct recovery_action_type {
    ECOA::uint32 value;
    enum EnumValues {
      SHUTDOWN = 0,
      COLD_RESTART = 1,
      WARM_RESTART = 2,
      CHANGE_DEPLOYMENT = 3
    };
    inline void operator = (ECOA::uint32 i) { value = i; }
    inline operator ECOA::uint32() const { return value; }
  };

  const ECOA::uint32 PINFO_FILENAME_MAXSIZE = 256;
  typedef struct {
     ECOA::uint32 current_size;
     ECOA::char8  data[ECOA::PINFO_FILENAME_MAXSIZE];
  } pinfo_filename;


  /* ECOA:seek_whence_type */
  struct seek_whence_type {
    ECOA::uint32 value;
    enum EnumValues {
      ECOA_SEEK_SET = 0,
      ECOA_SEEK_CUR = 1,
      ECOA_SEEK_END = 2
    };
    inline void operator = (ECOA::uint32 i) { value = i; }
    inline operator ECOA::uint32() const { return value; }
  };

} /* ECOA */

#endif /* __ECOA_HPP__ */

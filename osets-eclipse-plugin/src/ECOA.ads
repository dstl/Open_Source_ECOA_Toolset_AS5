package ECOA is

  type Boolean_8_Type is new Boolean;
  for Boolean_8_Type'Size use 8;
  type Character_8_Type is new Character;
  for Character_8_Type'Size use 8;
  type Signed_8_Type is range -127 .. 127;
  for Signed_8_Type'Size use 8;
  type Signed_16_Type is range -32767 .. 32767;
  for Signed_16_Type'Size use 16;
  type Signed_32_Type is range -2147483647 .. 2147483647;
  for Signed_32_Type'Size use 32;
  type Signed_64_Type is range -9223372036854775807 .. 9223372036854775807;
  for Signed_64_Type'Size use 64;
  type Unsigned_8_Type is mod 2 ** 8;
  for Unsigned_8_Type'Size use 8;
  type Unsigned_16_Type is mod 2 ** 16;
  for Unsigned_16_Type'Size use 16;
  type Unsigned_32_Type is mod 2 ** 32;
  for Unsigned_32_Type'Size use 32;
  type Unsigned_64_Type is mod 2 ** 64;
  for Unsigned_64_Type'Size use 64;
  type Float_32_Type is digits 6;
  for Float_32_Type'Size use 32;
  type Float_64_Type is digits 15;
  for Float_64_Type'Size use 64;
  type Byte_Type is mod 2 ** 8;
  for Byte_Type'Size use 8;

  type Return_Status_Type is new Unsigned_32_Type;
    Return_Status_Type_OK                        : constant Return_Status_Type := 0;
    Return_Status_Type_INVALID_HANDLE            : constant Return_Status_Type := 1;
    Return_Status_Type_DATA_NOT_INITIALIZED      : constant Return_Status_Type := 2;
    Return_Status_Type_NO_DATA                   : constant Return_Status_Type := 3;
    Return_Status_Type_INVALID_IDENTIFIER        : constant Return_Status_Type := 4;
    Return_Status_Type_NO_RESPONSE               : constant Return_Status_Type := 5;
    Return_Status_Type_OPERATION_ALREADY_PENDING : constant Return_Status_Type := 6;
    Return_Status_Type_INVALID_SERVICE_ID        : constant Return_Status_Type := 7;
    Return_Status_Type_CLOCK_UNSYNCHRONIZED      : constant Return_Status_Type := 8;
    Return_Status_Type_INVALID_TRANSITION        : constant Return_Status_Type := 9;
    Return_Status_Type_RESOURCE_NOT_AVAILABLE    : constant Return_Status_Type := 10;
    Return_Status_Type_OPERATION_NOT_AVAILABLE   : constant Return_Status_Type := 11;
    Return_Status_Type_PENDING_STATE_TRANSITION  : constant Return_Status_Type := 12;
    Return_Status_Type_INVALID_PARAMETER         : constant Return_Status_Type := 13;

  type Seconds_Type is mod 2 ** 32;
  for Seconds_Type'Size use 32;
  for Seconds_Type'Alignment use 4;
  
  type Nanoseconds_Type is range 0 .. 999999999;
  for Nanoseconds_Type'Size use 32;
  for Nanoseconds_Type'Alignment use 4;
  
  type HR_Time_Type is record
    Seconds     : Seconds_Type := 0;
    Nanoseconds : Nanoseconds_Type := 0;
  end record;
  for HR_Time_Type'size use 64;
  for HR_Time_Type'Alignment use 4;

  type Global_Time_Type is record
    Seconds     : Seconds_Type := 0;
    Nanoseconds : Nanoseconds_Type := 0;
  end record;
  for Global_Time_Type'size use 64;
  for Global_Time_Type'Alignment use 4;

  type Timestamp_Type is record
    Seconds     : Seconds_Type := 0;
    Nanoseconds : Nanoseconds_Type := 0;
  end record;
  for Timestamp_Type'size use 64;
  for Timestamp_Type'Alignment use 4;

  type Duration_Type is record
    Seconds     : Seconds_Type := 0;
    Nanoseconds : Nanoseconds_Type := 0;
  end record;
  for Duration_Type'size use 64;
  for Duration_Type'Alignment use 4;

  type Log_Elements_Index_Type is range 0 .. 255;
  for Log_Elements_Index_Type'size use 32;
  for Log_Elements_Index_Type'Alignment use 4;

  type Log_Elements_Type is array (Log_Elements_Index_Type) of ECOA.Character_8_Type;
  for Log_Elements_Type'size use 2048;
  for Log_Elements_Type'Alignment use 4;

  type Log_Type is record
    Current_Size : Log_Elements_Index_Type  := Log_Elements_Index_Type'First;
    Data         : Log_Elements_Type        := (others => ECOA.Character_8_Type'First);
  end record;
  for Log_Type'size use 2080;
  for Log_Type'Alignment use 4;

  type Module_States_Type is new Unsigned_32_Type;
    Module_States_Type_IDLE    : constant Module_States_Type := 0;
    Module_States_Type_READY   : constant Module_States_Type := 1;
    Module_States_Type_RUNNING : constant Module_States_Type := 2;

  type Module_Error_Type is new Unsigned_32_Type;
    Module_Error_Type_ERROR       : constant Module_Error_Type := 0;
    Module_Error_Type_FATAL_ERROR : constant Module_Error_Type := 1;

  type Error_Id_Type is new Unsigned_32_Type;

  type Asset_Id_Type is new Unsigned_32_Type;

  type Asset_Type is new Unsigned_32_Type;
    Asset_Type_COMPONENT         : constant Asset_Type := 0;
    Asset_Type_PROTECTION_DOMAIN : constant Asset_Type := 1;
    Asset_Type_NODE              : constant Asset_Type := 2;
    Asset_Type_PLATFORM          : constant Asset_Type := 3;
    Asset_Type_SERVICE           : constant Asset_Type := 4;
    Asset_Type_DEPLOYMENT        : constant Asset_Type := 5;

  type Error_Type is new Unsigned_32_Type;
    Error_Type_RESOURCE_NOT_AVAILABLE : constant Error_Type := 0;
    Error_Type_UNAVAILABLE            : constant Error_Type := 1;
    Error_Type_MEMORY_VIOLATION       : constant Error_Type := 2;
    Error_Type_NUMERICAL_ERROR        : constant Error_Type := 3;
    Error_Type_ILLEGAL_INSTRUCTION    : constant Error_Type := 4;
    Error_Type_STACK_OVERFLOW         : constant Error_Type := 5;
    Error_Type_DEADLINE_VIOLATION     : constant Error_Type := 6;
    Error_Type_OVERFLOW               : constant Error_Type := 7;
    Error_Type_UNDERFLOW              : constant Error_Type := 8;
    Error_Type_ILLEGAL_INPUT_ARGS     : constant Error_Type := 9;
    Error_Type_ILLEGAL_OUTPUT_ARGS    : constant Error_Type := 10;
    Error_Type_ERROR                  : constant Error_Type := 11;
    Error_Type_FATAL_ERROR            : constant Error_Type := 12;
    Error_Type_HARDWARE_FAULT         : constant Error_Type := 13;
    Error_Type_POWER_FAIL             : constant Error_Type := 14;
    Error_Type_COMMUNICATION_ERROR    : constant Error_Type := 15;
    Error_Type_INVALID_CONFIG         : constant Error_Type := 16;
    Error_Type_INITIALISATION_PROBLEM : constant Error_Type := 17;
    Error_Type_CLOCK_UNSYNCHRONIZED   : constant Error_Type := 18;
    Error_Type_UNKNOWN_OPERATION      : constant Error_Type := 19;
    Error_Type_OPERATION_OVERRATED    : constant Error_Type := 20;
    Error_Type_OPERATION_UNDERRATED   : constant Error_Type := 21;

  type Recovery_Action_Type is new Unsigned_32_Type;
    Recovery_Action_Type_SHUTDOWN          : constant Recovery_Action_Type := 0;
    Recovery_Action_Type_COLD_RESTART      : constant Recovery_Action_Type := 1;
    Recovery_Action_Type_WARM_RESTART      : constant Recovery_Action_Type := 2;
    Recovery_Action_Type_CHANGE_DEPLOYMENT : constant Recovery_Action_Type := 3;

  type Pinfo_Filename_Elements_Index_Type is range 0..255;
  type Pinfo_Filename_Elements_Type is array 
    (Pinfo_Filename_Elements_Index_Type) of ECOA.Character_8_Type;

  type Pinfo_Filename_Type is
    record
      Current_Size : Pinfo_Filename_Elements_Index_Type;
      Data         : Pinfo_Filename_Elements_Type;
    end record;
  for Pinfo_Filename_Elements_Type'size use 2048;
  for Pinfo_Filename_Elements_Type'Alignment use 4;

  type Seek_Whence_Type is new Unsigned_32_Type;
    Seek_Whence_Type_SEEK_SET       	    : constant Seek_Whence_Type := 0;
    Seek_Whence_Type_SEEK_CUR       	    : constant Seek_Whence_Type := 1;
    Seek_Whence_Type_SEEK_END	       	    : constant Seek_Whence_Type := 2;

end ECOA;

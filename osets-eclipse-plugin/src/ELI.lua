-- ECOA protocol
-- add this script to /home/username/.wireshark/plugins/

-- IDs.lua needs to populate a lookup table of EUIDs and Service Operation Name.

ID_Path =  "D:/IDs.lua"
dofile(ID_Path)

ID_list[0xFFFFFFFF] = "ALL"

--   Port_Header     : constant := 16#EC#;
--   EX1_To_EX2_Port : constant := 16#12#;
--   EX1_To_EX3_Port : constant := 16#13#;
--   EX2_To_EX1_Port : constant := 16#21#;
--   EX3_To_EX1_Port : constant := 16#31#;
--   EX2_To_EX3_Port : constant := 16#23#;
--   EX3_To_EX2_Port : constant := 16#32#;

Port_list = {
 [0xEC12] = "EX1_To_EX2",
 [0xEC13] = "EX1_To_EX3",
 [0xEC21] = "EX2_To_EX1",
 [0xEC23] = "EX2_To_EX3",
 [0xEC31] = "EX3_To_EX1",
 [0xEC32] = "EX3_To_EX2"
}

Port_list[0xFFFF] = "????"


-- TODO: add fields properly. 
-- TODO: incorporate multiple protocols (replace tree:add(buffer(x,x)...) with tree:add(service_prot,...)
-- declare our protocol
eli_proto = Proto("ELI","ELI Protocol")

local f_control=ProtoField.uint8("ecoa.control","Control",base.HEX)
local f_mark=ProtoField.uint16("ecoa.mark","Mark",base.HEX)

eli_proto.fields = {f_control,f_mark}
local orig_UDP_Dissector

local MESSAGES = {  [1] = 'Status',
					[2] = 'Status_Request',
					[3] = 'Availability_Status',
					[4] = 'Availability_Request',
					[5] = 'Unknown_Op_Response',
					[6] = 'Service_Not_Available',
					[7] = 'Versioned_Data_Pull',
					[8] = 'Composite_Change_Request',
					[9] = 'Composit_Change_Ack' }
function portName(udp_dest_port)
	
	if Port_list[udp_dest_port] ~= nil then
		return Port_list[udp_dest_port]
	else
		return "dest: "..udp_dest_port
	end
end



function payloadAsBytes(buffer, pinfo, size, tree)
	for i=0,size-1 do
		tree:add(buffer(i,1), i..": "..buffer(i,1))
	end
end

function payloadAsInts(buffer, pinfo, size, tree)
	mySize = size / 4
	for i=0,mySize-1 do
		tree:add(buffer(4 * i,4), i..": "..buffer(4*i,4):uint())
	end
end

-- HANDLE SYSTEM SPECIFIC SERVICE OPS
function serviceOp(buffer, pinfo, size, tree, UID_name)
	-- Add user code for each operation?
	-- Get user operations from "IDs.lua"??
	payloadAsBytes(buffer, pinfo, size, tree)
end

-- PLATFORM STATUS MESSAGE
function statusPayload(buffer, pinfo, size, tree)
	local status 
	if buffer(0,4):uint() == 1 then
		status = " UP"
	else
		status = " DOWN"
	end
	tree:add(buffer(0,4),"Status:"..status)

	local composite = buffer(4,4):uint()
	tree:add(buffer(4,4),"CompositeID: "..ID_list[composite])

	pinfo.cols.info:append(status)
end

-- SERVICE AVAILABILITY MESSAGE
function availabilityPayload(buffer, pinfo, size, tree)
	
    local section = tree:add(buffer(0,size),"Availability Payload")
	local numServices = buffer(0,4):uint()
	section:add(buffer(0,4),"Num Services: "..numServices)
	pinfo.cols.info:append(" x"..numServices..": ")

	local subtree
	local avail
	local offset = 4
	
	if numServices == 1 then
		  subtree = section:add(buffer(offset,8), "Availability")
		  subtree:add(buffer(offset,4), ID_list[buffer(offset, 4):uint()])
		  if buffer(offset+4,4):uint() == 0 then
		  	avail = "DOWN"
		  else
			avail = "UP"
		  end
		  subtree:add(buffer(offset+4,4), "Available: "..avail)
		  pinfo.cols.info:append( ID_list[buffer(offset, 4):uint()]..": "..avail)
		  offset = offset + 8
	else
		for i=0,numServices-1 do
		  subtree = section:add(buffer(offset,8), "Availability")
		  subtree:add(buffer(offset,4), ID_list[buffer(offset, 4):uint()])
		  if buffer(offset+4,4):uint() == 0 then
		  	avail = "DOWN"
		  else
			avail = "UP"
		  end
		  subtree:add(buffer(offset+4,4), "Available: "..avail)
		  pinfo.cols.info:append(avail..",")
		  offset = offset + 8
		end	
	end
	
end

-- SERVICE REQUEST MESSAGE
function availRequestPayload(buffer, pinfo, size, tree)
	local ID_Name = ID_list[ buffer(0,4):uint() ]
	tree:add(buffer(0,4),"Service Requested: "..ID_Name)
	pinfo.cols.info:append(": "..ID_Name)
end

-- UNKNOWN OPERATION MESSAGE
function unknownPayload(buffer, pinfo, size, tree)
	local ID_Name = ID_list[ buffer(0,4):uint() ]
	tree:add(buffer(0,4),"Unknown Op: "..ID_Name)
	pinfo.cols.info:append(" Op: "..ID_Name)
end

-- SERVICE NOT AVAILABLE MESSAGE
function serviceUnavailablePayload(buffer, pinfo, size, tree)
	local ID_Name = ID_list[ buffer(0,4):uint() ]
	tree:add(buffer(0,4),"Unavailable Service: "..ID_Name)
	pinfo.cols.info:append(": "..ID_Name)
end

-- VERSIONED DATA PULL MESSAGE
function versionPullPayload(buffer, pinfo, size, tree)
	local ID_Name = ID_list[ buffer(0,4):uint() ]
	tree:add(buffer(0,4),"Versioned Data: "..ID_Name)
	pinfo.cols.info:append(": "..ID_Name)
end


function  dissect_eli(buffer,pinfo,tree)
    pinfo.cols.protocol = "ELI"

    local subtree = tree:add(eli_proto,buffer(),"ELI Message")

 	local hdr = subtree:add(buffer(0,3), "ecoa header")
    hdr:add(buffer(0,2),"Mark: " ..buffer(0,2))
    -- Big endian - are the 'first' bits 0..3?
    hdr:add(buffer(2,1),"Version "..buffer(2,1):bitfield(0,4))
    
	local domain = buffer(2,1):bitfield(4,4)
    hdr:add(buffer(2,1),"Domain ".. domain)


    local UID_lookup
    
    local paySize = buffer(16,4):uint()
    local message = 'Service'
    
	if domain == 0 then
		--  Platform level management
 		local PF_ID = buffer(3,1):uint()
 	   subtree:add(buffer(3,1), "Platform ID: " .. PF_ID)  
 	   message =  MESSAGES[buffer(4,4):uint()]
 	   subtree:add(buffer(4,4), "Platform Message: " .. message)
	    pinfo.cols.info:append(" "..message)
	
	elseif domain == 1 then
		-- Service Operations
	    subtree:add(buffer(3,1), "Sender ID: " .. buffer(3,1):uint())  
	    UID_lookup = ID_list[ buffer(4,4):uint() ]
	    subtree:add(buffer(4,4),"Service Op UID: " .. UID_lookup )
	    pinfo.cols.info:append(" Service: "..UID_lookup)
	
	elseif domain == 2 then
		-- Protection Domain
		local PD_ID = buffer(3,1):uint()
	    subtree:add(buffer(3,1), "Sender Prot Domain ID: " .. PD_ID)  
	    message =  MESSAGES[buffer(4,4):uint()]
	    subtree:add(buffer(4,4),"Protection Domain Message: " .. message)
	    pinfo.cols.info:append(" FromPD "..PD_ID.. " "..message)
	end
    
	local time_subtree = hdr:add(buffer(8,8), "Time")
    time_subtree:add(buffer(8,4), "Sec: " .. buffer(8,4):uint())
    time_subtree:add(buffer(12,4), "Nsec: " .. buffer(12,4):uint())
    
    hdr:add(buffer(16,4), "Payload Size: " .. paySize)
    subtree:add(buffer(20,4), "Seq Num: " .. buffer(20,4):uint())

    if paySize > 0 then
		if message == 'Status' then
			statusPayload(buffer(24,paySize), pinfo, paySize, subtree)
    	elseif message == 'Status_Request' then
			-- no payload
    	elseif message == 'Availability_Status' then
 			availabilityPayload(buffer(24,paySize), pinfo, paySize, subtree)
     	elseif message == 'Availability_Request' then
 			availRequestPayload(buffer(24,paySize), pinfo, paySize, subtree)
    	elseif message == 'Unknown_Op_Response' then
 			unknownPayload(buffer(24,paySize), pinfo, paySize, subtree)
    	elseif message == 'Service_Not_Available' then
  			serviceUnavailablePayload(buffer(24,paySize), pinfo, paySize, subtree)
  	 	elseif message == 'Versioned_Data_Pull' then
  			versionPullPayload(buffer(24,paySize), pinfo, paySize, subtree)
  	 	elseif message == 'Service' then
  	 	 	serviceOp(buffer(24,paySize), pinfo, paySize, subtree, UID_lookup)
		else
    		subtree = subtree:add(buffer(24,paySize),"Payload")
    		-- user decide
    		-- payloadAsBytes(buffer(24,paySize),pinfo, paySize, subtree)
    		payloadAsInts(buffer(24,paySize),pinfo, paySize, subtree)
		end
	end
end


function eli_proto.dissector(buffer,pinfo,tree)
	-- daisy chain the udp dissector
    orig_UDP_Dissector:call(buffer,pinfo,tree)
    
    local ecoa_mark  = buffer(12, 2):uint()
    if (ecoa_mark == 0xec0a) then
    	local udp_dest_port = buffer(2, 2):uint()
    	local udp_length = buffer(4, 2):uint()
    	
    	
    	
    	pinfo.cols.info:set(portName(udp_dest_port))
  	
       	dissect_eli(buffer(12, udp_length-12), pinfo, tree)
--     else
--     	orig_UDP_Dissector:call(buffer,pinfo,tree)
    end
end

-- allow ECOA messages to be captured on any udp packet
local UDP_IP_Proto = 17
local orig_UDP_Table = DissectorTable.get("ip.proto")

-- daisy chain with the standard udp dissector from the ip.proto table
orig_UDP_Dissector = orig_UDP_Table:get_dissector(UDP_IP_Proto)
-- NO, WE're handling udp thanks.
orig_UDP_Table:add(UDP_IP_Proto, eli_proto)


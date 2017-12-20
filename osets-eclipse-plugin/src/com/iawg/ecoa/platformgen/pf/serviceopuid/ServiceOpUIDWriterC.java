/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.serviceopuid;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp.EventDirection;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class ServiceOpUIDWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_01 = "#define ";
	private SM_LogicalComputingPlatform lcp;

	public ServiceOpUIDWriterC(Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.lcp = lcp;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(lcp.getName() + "_Service_Op_UID.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#SERVICE_OP_UID_DEFS#" + LF;
		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText = "/* File " + lcp.getName() + "_Service_Op_UID.h */" + LF;

		// Replace the #PREAMBLE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeServiceUIDDefs() {
		String serviceOpUIDText = "/* Define an ID for each service operation UID */" + LF + LF +

				" /* Incoming UIDs */" + LF;
		// First process any outgoing UIDs
		for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
			for (SM_ComponentInstance compInst : pd.getComponentInstances()) {
				// Get the target wires (where compInst is a provider)
				for (SM_Wire wire : compInst.getTargetWires()) {
					// Get the service associated with the wire
					SM_ServiceInstance serviceInst = wire.getTargetOp();

					// Events
					for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
						if (eventOp.getDirection() == EventDirection.RECEIVED_BY_PROVIDER) {
							SM_UIDServiceOp uid = wire.getUID(eventOp);

							serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Incoming Event */" + LF;
						} else {
							SM_UIDServiceOp uid = wire.getUID(eventOp);

							serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Outgoing Event */" + LF;
						}
					}

					// Request received's (server)
					for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
						SM_UIDServiceOp uid = wire.getUID(requestOp);

						serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Incoming(request)/Outgoing(response) */" + LF;
					}

					// Versioned data (writers)
					for (SM_DataServiceOp dataOp : serviceInst.getServiceInterface().getDataOps()) {
						SM_UIDServiceOp uid = wire.getUID(dataOp);

						serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Outgoing VD */" + LF;
					}

				}

				// Get the source wires (where compInst is a requirer)
				for (SM_Wire wire : compInst.getSourceWires()) {

					// Get the service associated with the wire
					SM_ServiceInstance serviceInst = wire.getSourceOp();

					// Events
					for (SM_EventServiceOp eventOp : serviceInst.getServiceInterface().getEventOps()) {
						SM_UIDServiceOp uid = wire.getUID(eventOp);

						if (eventOp.getDirection() == EventDirection.SENT_BY_PROVIDER) {
							serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Incoming Event */" + LF;
						} else {
							serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Outgoing Event */" + LF;
						}
					}

					// Request received response's (client)
					for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
						SM_UIDServiceOp uid = wire.getUID(requestOp);

						serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Outgoing(request)/Incoming(response) */" + LF;
					}

					// Versioned data updates
					for (SM_DataServiceOp dataOp : serviceInst.getServiceInterface().getDataOps()) {
						SM_UIDServiceOp uid = wire.getUID(dataOp);

						serviceOpUIDText += SEP_PATTERN_01 + uid.getUIDDefString() + " " + uid.getID() + " /* Incoming VD */" + LF;
					}
				}
			}
		}

		// Replace the #SERVICE_OP_UID_DEFS# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#SERVICE_OP_UID_DEFS#", serviceOpUIDText);

	}

}

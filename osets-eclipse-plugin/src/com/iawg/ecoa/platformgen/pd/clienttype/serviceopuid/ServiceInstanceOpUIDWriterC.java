/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.clienttype.serviceopuid;

import java.nio.file.Path;
import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

public class ServiceInstanceOpUIDWriterC extends SourceFileWriter {
	private static final Logger LOGGER = LogManager.getLogger(ServiceInstanceOpUIDWriterC.class);
	private SM_ComponentImplementation compImpl;
	private ArrayList<String> includeList = new ArrayList<String>();

	public ServiceInstanceOpUIDWriterC(Path outputDir, SM_ComponentImplementation compImpl) {
		super(outputDir);
		this.compImpl = compImpl;

		setFileStructure();
	}

	@Override
	public void close() {
		String closeText = "#endif  /* _" + compImpl.getName().toUpperCase() + "_SERVICE_INSTANCE_OPERATION_UID */" + LF + LF;

		// Replace the #CLOSE# tag in string builder.
		WriterSupport.replaceText(codeStringBuilder, "#CLOSE#", closeText);

		super.close();
	}

	private String generateUID(ArrayList<Integer> UIDList, SM_ServiceInstance service, SM_Object operation) {
		// UID string built up as "serviceInstanceName:operationName"
		String uidHashName = service.getName() + ":" + operation.getName();
		Integer uid = Math.abs(uidHashName.hashCode());

		boolean added = false;
		while (!added) {
			if (UIDList.contains(uid)) {
				LOGGER.info("Warning - Duplicate Service Instance Operation UID has been found - adding 1 to hash value");
				uid++;
			} else {
				UIDList.add(uid);
				added = true;
			}
		}

		return "#define " + compImpl.getName().toUpperCase() + "_" + service.getName().toUpperCase() + "_" + operation.getName().toUpperCase() + "_UID " + uid + LF;
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(compImpl.getName() + "_Service_Instance_Operation_UID.h"));
	}

	@Override
	protected void setFileStructure() {
		String fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#UIDS#" + LF + "#CLOSE#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeIncludes() {
		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writePreamble() {
		String preambleText = "/* File " + compImpl.getName() + "_Service_Instance_Operation_UID.h */" + LF + LF +

				"#if !defined(_" + compImpl.getName().toUpperCase() + "_SERVICE_INSTANCE_OPERATION_UID)" + LF + "#define _" + compImpl.getName().toUpperCase() + "_SERVICE_INSTANCE_OPERATION_UID" + LF + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeUIDs() {
		ArrayList<Integer> UIDList = new ArrayList<Integer>();
		StringBuilder uidText = new StringBuilder();
		uidText.append("/* Define the Service Instance Operation UIDs */" + LF + LF +

				"/* Provided Service Op UIDs */" + LF + LF);

		for (SM_ServiceInstance provServ : compImpl.getCompType().getServiceInstancesList()) {
			uidText.append("/* Provided Service Op UIDs for " + provServ.getName() + " */" + LF);

			for (SM_RRServiceOp requestOp : provServ.getServiceInterface().getRROps()) {
				uidText.append(generateUID(UIDList, provServ, requestOp));
			}
			for (SM_EventServiceOp eventOp : provServ.getServiceInterface().getEventOps()) {
				uidText.append(generateUID(UIDList, provServ, eventOp));
			}
		}

		uidText.append("/* Required Service Op UIDs */" + LF + LF);

		for (SM_ServiceInstance reqServ : compImpl.getCompType().getReferenceInstancesList()) {
			uidText.append("/* Required Service Op UIDs for " + reqServ.getName() + " */" + LF);

			for (SM_RRServiceOp requestOp : reqServ.getServiceInterface().getRROps()) {
				uidText.append(generateUID(UIDList, reqServ, requestOp));
			}
			for (SM_EventServiceOp eventOp : reqServ.getServiceInterface().getEventOps()) {
				uidText.append(generateUID(UIDList, reqServ, eventOp));
			}
		}

		// Replace the #UIDS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#UIDS#", uidText.toString());
	}

}

/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.servicemanager;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;
import com.iawg.ecoa.systemmodel.uid.SM_UIDServiceOp;

public class ServiceManagerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_71 = "ECOA__return_status ";
	private boolean isHeader;
	private SM_ProtectionDomain protectionDomain;
	private PlatformGenerator platformGenerator;

	private ArrayList<String> includeList = new ArrayList<String>();

	public ServiceManagerWriterC(PlatformGenerator platformGenerator, boolean isHeader, Path outputDir, SM_ProtectionDomain protectionDomain) {
		super(outputDir);
		this.isHeader = isHeader;
		this.protectionDomain = protectionDomain;
		this.platformGenerator = platformGenerator;

		setFileStructure();
	}

	private String generateServiceAvailabilityCase(SM_ComponentInstance compInst, SM_ServiceInstance serviceInstance) {
		boolean requirerOnProtectionDomain = false;

		String serviceAvailCaseString = "";

		String serviceUIDString = compInst.getName().toUpperCase() + "_" + serviceInstance.getName().toUpperCase() + "_UID";

		serviceAvailCaseString += "      case " + serviceUIDString + ":" + LF + "         /* Call any requiring components' service API (if local to protection domain) */" + LF;

		for (SM_Wire wire : compInst.getTargetWires(serviceInstance)) {
			// Determine if destination component is in this protection domain
			// or not
			if (wire.getSource().getProtectionDomain() == protectionDomain) {
				requirerOnProtectionDomain = true;

				String destServiceAPIName = wire.getSource().getName() + "_" + wire.getSourceOp().getName();

				// The destination is local - call the service operation
				// directly.
				serviceAvailCaseString += "         " + destServiceAPIName + "__Service_Availability_Changed(&opTimestamp);" + LF;

				includeList.add(destServiceAPIName + "_Controller");
			}
		}

		serviceAvailCaseString += "         break;" + LF;

		// If not at least one requirer is on our protection domain, return an
		// empty string as do not need a case for this UID...
		if (!requirerOnProtectionDomain) {
			serviceAvailCaseString = "";
		}

		return serviceAvailCaseString;

	}

	@Override
	public void open() {

		if (isHeader) {
			super.openFile(outputDir.resolve(protectionDomain.getName() + "_Service_Manager.h"));
		} else {
			super.openFile(outputDir.resolve(protectionDomain.getName() + "_Service_Manager.c"));
		}
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		if (isHeader) {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SERV_AVAIL_STRUCT#" + LF + "#GET_SERV_AVAIL#" + LF + "#SET_SERV_AVAIL#" + LF + "#SET_PROVIDER_UNAVAIL#" + LF + "#INITIALISE#" + LF;
		} else {
			fileStructure = "#PREAMBLE#" + LF + "#INCLUDES#" + LF + "#SERV_AVAIL_DECL#" + LF + "#GET_SERV_AVAIL#" + LF + "#SET_SERV_AVAIL#" + LF + "#SET_PROVIDER_UNAVAIL#" + LF + "#INITIALISE#" + LF;
		}

		codeStringBuilder.append(fileStructure);
	}

	public void writeGetServiceAvail() {
		String getServiceAvailText = SEP_PATTERN_71 + protectionDomain.getName() + "_Service_Manager__Get_Availability(ECOA__uint32 providedServiceUID, ECOA__boolean8 *availability)";

		if (isHeader) {
			getServiceAvailText += ";" + LF + LF;
		} else {
			// Generate the get service availability function.
			getServiceAvailText += LF + "{" + LF + "   int servInst;" + LF + "   for (servInst = 0; servInst <= Provided_Services_MAXSIZE; servInst++)" + LF + "   {" + LF + "      if (" + protectionDomain.getName() + "_serviceAvailabilityList[servInst].uid == providedServiceUID)" + LF + "      {" + LF + "         *availability = " + protectionDomain.getName() + "_serviceAvailabilityList[servInst].available;" + LF + "         return ECOA__return_status_OK;" + LF + "      }" + LF + "   }" + LF + LF +

					"   /* Failed to find the service... */" + LF + "   *availability = ECOA__FALSE;" + LF + "   return ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "}" + LF;

		}

		// Replace the #GET_SERV_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_SERV_AVAIL#", getServiceAvailText);
	}

	public void writeIncludes() {

		if (isHeader) {
			includeList.add("ECOA");
		} else {
			includeList.add(protectionDomain.getName() + "_Service_Manager");
			includeList.add("Service_UID");
			includeList.add(protectionDomain.getName() + "_Service_Op_UID");
			includeList.add("Component_Instance_ID");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {
		String initialiseText = "";

		if (isHeader) {
			initialiseText += "void " + protectionDomain.getName() + "_Service_Manager__Initialise();" + LF + LF;
		} else {
			// Generate the Initialise function.
			initialiseText += "void " + protectionDomain.getName() + "_Service_Manager__Initialise()" + LF + "{" + LF + LF +

					"   /* Initialise the availability list */" + LF + LF;

			int providedServiceNum = 0;
			for (SM_ComponentInstance compInst : platformGenerator.getSystemModel().getFinalAssembly().getComponentInstances()) {
				for (SM_ServiceInstance si : compInst.getCompType().getServiceInstancesList()) {
					String UIDString = compInst.getName().toUpperCase() + "_" + si.getName().toUpperCase() + "_UID";

					initialiseText += "   " + protectionDomain.getName() + "_serviceAvailabilityList[" + providedServiceNum + "].uid = " + UIDString + ";" + LF + "   " + protectionDomain.getName() + "_serviceAvailabilityList[" + providedServiceNum + "].available = ECOA__FALSE;" + LF;
					providedServiceNum++;
				}
			}

			initialiseText += "}" + LF;

		}

		// Replace the #INITIALISE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INITIALISE#", initialiseText);
	}

	public void writePreamble() {
		String preambleText = "";

		if (isHeader) {
			preambleText += "/* File " + protectionDomain.getName() + "_Service_Manager.h */" + LF;
		} else {
			preambleText += "/* File " + protectionDomain.getName() + "_Service_Manager.c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeServiceAvailabilityStruct() {
		// Get the number of provided services in the system
		int numProvidedServices = 0;
		for (SM_ComponentInstance compInst : platformGenerator.getSystemModel().getFinalAssembly().getComponentInstances()) {
			numProvidedServices += compInst.getCompType().getServiceInstancesList().size();
		}

		String servAvailStructText = "/* Define the Service Availability Structure */" + LF +

				"typedef struct {" + LF + "   ECOA__int32 uid;" + LF + "   ECOA__boolean8 available;" + LF + "} Service_Availability_Type;" + LF + LF +

				"#define Provided_Services_MAXSIZE " + numProvidedServices + LF + "typedef Service_Availability_Type Service_Availability_List_Type[Provided_Services_MAXSIZE];" + LF + LF;

		// Replace the #SERV_AVAIL_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SERV_AVAIL_STRUCT#", servAvailStructText);

	}

	public void writeServiceAvailablityDecl() {
		String servAvailDeclText = "/* Declare the Service Availability List */" + LF + "static Service_Availability_List_Type " + protectionDomain.getName() + "_serviceAvailabilityList;" + LF + LF;

		// Replace the #SERV_AVAIL_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SERV_AVAIL_DECL#", servAvailDeclText);

	}

	public void writeSetProviderUnavailable() {
		String setProviderUnavailText = SEP_PATTERN_71 + protectionDomain.getName() + "_Service_Manager__Set_Provider_Unavailabile(ECOA__uint32 serviceOpUID)";

		if (isHeader) {
			setProviderUnavailText += ";" + LF + LF;
		} else {
			// Generate the set provider unavailable function.
			setProviderUnavailText += LF + "{" + LF + "   ECOA__uint32 providerServiceUID;" + LF + LF +

					"   switch (serviceOpUID)" + LF + "   {" + LF;

			for (SM_ComponentInstance compInst : protectionDomain.getComponentInstances()) {
				// Get the source wires (where compInst is a requirer)
				for (SM_Wire wire : compInst.getSourceWires()) {
					// Get the service associated with the wire
					SM_ServiceInstance serviceInst = wire.getTargetOp();
					SM_ComponentInstance providerCompInst = wire.getTarget();

					// Request received response's (client)
					for (SM_RRServiceOp requestOp : serviceInst.getServiceInterface().getRROps()) {
						SM_UIDServiceOp uid = wire.getUID(requestOp);

						setProviderUnavailText += "      case " + uid.getUIDDefString() + " : " + LF + "         providerServiceUID = " + providerCompInst.getName().toUpperCase() + "_" + serviceInst.getName().toUpperCase() + "_UID;" + LF + "         break;" + LF;
					}
				}
			}

			setProviderUnavailText += "   }" + LF + LF +

					"   /* Set the provider unavailable */" + LF + "   " + protectionDomain.getName() + "_Service_Manager__Set_Availability(providerServiceUID, ECOA__FALSE);" + LF + LF +

					// TODO - amend logic for return status?!
					"   return ECOA__return_status_OK;" + LF + "}" + LF;
		}

		// Replace the #SET_PROVIDER_UNAVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_PROVIDER_UNAVAIL#", setProviderUnavailText);
	}

	public void writeSetServiceAvail() {
		String setServiceAvailText = SEP_PATTERN_71 + protectionDomain.getName() + "_Service_Manager__Set_Availability(ECOA__uint32 providedServiceUID, ECOA__boolean8 availability)";

		if (isHeader) {
			setServiceAvailText += ";" + LF + LF;
		} else {
			includeList.add("ECOA_time_utils");

			// Generate the set service availability function.
			setServiceAvailText += LF + "{" + LF + "   int servInst;" + LF + "   ECOA__timestamp opTimestamp;" + LF + "   ECOA_setTimestamp(&opTimestamp);" + LF + LF +

					"   /* Update the service list */" + LF + "   for (servInst = 0; servInst <= Provided_Services_MAXSIZE; servInst++)" + LF + "   {" + LF + "      if (" + protectionDomain.getName() + "_serviceAvailabilityList[servInst].uid == providedServiceUID)" + LF + "      {" + LF + "         " + protectionDomain.getName() + "_serviceAvailabilityList[servInst].available = availability;" + LF + "         break;" + LF +
					// TODO - remove this or amend logic...
					// " return ECOA__return_status_OK;" + LF +
					"      }" + LF + "   }" + LF + LF +
					// TODO - remove this or amend logic...
					// " /* Failed to find the service... */" + LF +
					// " return ECOA__return_status_OPERATION_NOT_AVAILABLE;" +
					// LF +

					"   /* Kick-off the notification process (for availability changed/provider changed notifications) */" + LF + "   switch (providedServiceUID)" + LF + "   {" + LF;

			// Generate a case for each provided service instance (unless no
			// requirers on the protection domain)
			for (SM_ComponentInstance compInst : platformGenerator.getSystemModel().getFinalAssembly().getComponentInstances()) {
				// Only process provided services.
				for (SM_ServiceInstance serviceInstance : compInst.getCompType().getServiceInstancesList()) {
					setServiceAvailText += generateServiceAvailabilityCase(compInst, serviceInstance);
				}
			}

			setServiceAvailText += "   }" + LF + LF +

			// TODO - amend logic for return status?!
					"   return ECOA__return_status_OK;" + LF + "}" + LF;

		}

		// Replace the #SET_SERV_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_SERV_AVAIL#", setServiceAvailText);
	}

}

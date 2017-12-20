/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.servicemanager;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.CLanguageSupport;
import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.assembly.SM_Wire;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class ServiceManagerWriterC extends SourceFileWriter {
	private static final String SEP_PATTERN_71 = "ECOA__return_status ";
	private boolean isHeader;
	private SM_LogicalComputingPlatform lcp;
	private PlatformManagerGenerator pfManagerGenerator;
	private String pfServiceManName;

	private ArrayList<String> includeList = new ArrayList<String>();

	public ServiceManagerWriterC(PlatformManagerGenerator pfManagerGenerator, boolean isHeader, Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.isHeader = isHeader;
		this.lcp = lcp;
		this.pfManagerGenerator = pfManagerGenerator;
		pfServiceManName = lcp.getName() + "_PF_Service_Manager";

		setFileStructure();
	}

	// TODO - need to ammend this to send the update to the correct PD?!
	private String generateServiceAvailabilityCase(SM_ComponentInstance compInst, SM_ServiceInstance serviceInstance) {
		boolean caseRequired = false;

		String serviceAvailCaseString = "";

		includeList.add(lcp.getName() + "_PFtoPF_Manager");

		String serviceUIDString = compInst.getName().toUpperCase() + "_" + serviceInstance.getName().toUpperCase() + "_UID";

		serviceAvailCaseString += "      case " + serviceUIDString + ":" + LF;

		for (SM_Wire wire : compInst.getTargetWires(serviceInstance)) {
			// Determine if requirer component is in this platform
			if (lcp.getAllProtectionDomains().contains(wire.getSource().getProtectionDomain())) {
				// Requirer is in our platform - generate a case only if the
				// provider is not within the platform!
				if (!lcp.getAllProtectionDomains().contains(wire.getTarget().getProtectionDomain())) {
					caseRequired = true;
					includeList.add("PD_IDS");
					includeList.add(lcp.getName() + "_PFtoPD_Manager");
					serviceAvailCaseString += "         /* The provider was from a remote platform, distribute the availability to a local protection domains with requirer */" + LF + "         " + lcp.getName() + "_PFtoPD_Manager__Send_Single_Service_Availability(providedServiceUID, availability, &opTimestamp, PD_IDS__" + wire.getSource().getProtectionDomain().getName().toUpperCase() + ");" + LF + LF;
				}
			}
			// Determine if provider is in this platform
			else if (lcp.getAllProtectionDomains().contains(wire.getTarget().getProtectionDomain())) {
				// Provider is in our platform - generate a case only if the
				// requirer is not within the platform!
				if (!lcp.getAllProtectionDomains().contains(wire.getSource().getProtectionDomain())) {
					caseRequired = true;
					long remotePlatformID = wire.getSource().getProtectionDomain().getLogicalComputingNode().getLogicalComputingPlatform().getRelatedUDPBinding().getPlatformID();

					serviceAvailCaseString += "         /* The provider is local to our platform, and requirer is a remote platform - send it to the remote platform! */" + LF + "         " + lcp.getName() + "_PFtoPF_Manager__Send_Single_Service_Availability(providedServiceUID, availability, &opTimestamp, " + remotePlatformID + ");" + LF + LF;
				}
			}
		}

		serviceAvailCaseString += "         break;" + LF;

		// If no case is required, return an empty string
		if (!caseRequired) {
			serviceAvailCaseString = "";
		}

		return serviceAvailCaseString;

	}

	@Override
	public void open() {

		if (isHeader) {
			super.openFile(outputDir.resolve(pfServiceManName + ".h"));
		} else {
			super.openFile(outputDir.resolve(pfServiceManName + ".c"));
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
		String getServiceAvailText = SEP_PATTERN_71 + pfServiceManName + "__Get_Availability(ECOA__uint32 providedServiceUID, ECOA__boolean8 *availability)";

		if (isHeader) {
			getServiceAvailText += ";" + LF + LF;
		} else {
			// Generate the get service availability function.
			getServiceAvailText += LF + "{" + LF + "   int servInst;" + LF + "   for (servInst = 0; servInst <= Provided_Services_MAXSIZE; servInst++)" + LF + "   {" + LF + "      if (" + lcp.getName() + "_serviceAvailabilityList[servInst].uid == providedServiceUID)" + LF + "      {" + LF + "         *availability = " + lcp.getName() + "_serviceAvailabilityList[servInst].available;" + LF + "         return ECOA__return_status_OK;" + LF + "      }" + LF + "   }" + LF + LF +

					"   /* Failed to find the service... */" + LF + "   *availability = ECOA__FALSE;" + LF + "   return ECOA__return_status_OPERATION_NOT_AVAILABLE;" + LF + "}" + LF;

		}

		// Replace the #GET_SERV_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GET_SERV_AVAIL#", getServiceAvailText);
	}

	public void writeIncludes() {

		if (isHeader) {
			includeList.add("ECOA");
		} else {
			includeList.add(pfServiceManName);
			includeList.add("Service_UID");
			// includeList.add(lcp.getName() + "_Service_Op_UID");
			includeList.add("Component_Instance_ID");
			includeList.add("ecoaLog");
			includeList.add("stdio");
		}

		String includeText = CLanguageSupport.generateIncludes(includeList);

		// Replace the #INCLUDES# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#INCLUDES#", includeText);
	}

	public void writeInitialise() {
		String initialiseText = "";

		if (isHeader) {
			initialiseText += "void " + pfServiceManName + "__Initialise();" + LF + LF;
		} else {
			// Generate the Initialise function.
			initialiseText += "void " + pfServiceManName + "__Initialise()" + LF + "{" + LF + LF +

					"   /* Initialise the availability list */" + LF + LF;

			int providedServiceNum = 0;
			for (SM_ComponentInstance compInst : pfManagerGenerator.getSystemModel().getFinalAssembly().getComponentInstances()) {
				for (SM_ServiceInstance si : compInst.getCompType().getServiceInstancesList()) {
					String UIDString = compInst.getName().toUpperCase() + "_" + si.getName().toUpperCase() + "_UID";

					initialiseText += "   " + lcp.getName() + "_serviceAvailabilityList[" + providedServiceNum + "].uid = " + UIDString + ";" + LF + "   " + lcp.getName() + "_serviceAvailabilityList[" + providedServiceNum + "].available = ECOA__FALSE;" + LF;
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
			preambleText += "/* File " + pfServiceManName + ".h */" + LF;
		} else {
			preambleText += "/* File " + pfServiceManName + ".c */" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeServiceAvailabilityStruct() {
		// Get the number of provided services in the system
		int numProvidedServices = 0;
		for (SM_ComponentInstance compInst : pfManagerGenerator.getSystemModel().getFinalAssembly().getComponentInstances()) {
			numProvidedServices += compInst.getCompType().getServiceInstancesList().size();
		}

		String servAvailStructText = "/* Define the Service Availability Structure */" + LF +

				"typedef struct {" + LF + "   ECOA__int32 uid;" + LF + "   ECOA__boolean8 available;" + LF + "} Service_Availability_Type;" + LF + LF +

				"#define Provided_Services_MAXSIZE " + numProvidedServices + LF + "typedef Service_Availability_Type Service_Availability_List_Type[Provided_Services_MAXSIZE];" + LF + LF;

		// Replace the #SERV_AVAIL_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SERV_AVAIL_STRUCT#", servAvailStructText);

	}

	public void writeServiceAvailablityDecl() {
		String servAvailDeclText = "/* Declare the Service Availability List */" + LF + "static Service_Availability_List_Type " + lcp.getName() + "_serviceAvailabilityList;" + LF + LF;

		// Replace the #SERV_AVAIL_STRUCT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SERV_AVAIL_DECL#", servAvailDeclText);

	}

	public void writeSetProviderUnavailable() {
		String setProviderUnavailText = SEP_PATTERN_71 + pfServiceManName + "__Set_Provider_Unavailabile(ECOA__uint32 serviceOpUID)";

		if (isHeader) {
			setProviderUnavailText += ";" + LF + LF;
		} else {
			// Generate the set provider unavailable function.
			setProviderUnavailText += LF + "{" + LF + "   ECOA__uint32 providerServiceUID;" + LF + LF +

					"   switch (serviceOpUID)" + LF + "   {" + LF;

			// TODO - need to handle setting of service unavailable (think this
			// if for requests that have timed out?!)
			// for (SM_ComponentInstance compInst :
			// protectionDomain.getComponentInstances())
			// {
			// // Get the source wires (where compInst is a requirer)
			// for (SM_Wire wire : compInst.getSourceWires())
			// {
			// // Get the service associated with the wire
			// SM_ServiceInstance serviceInst = wire.getTargetOp();
			// SM_ComponentInstance providerCompInst = wire.getTarget();
			//
			// // Request received response's (client)
			// for (SM_RRServiceOp requestOp :
			// serviceInst.getServiceInterface().getRROps())
			// {
			// SM_UIDServiceOp uid = wire.getUID(requestOp);
			//
			// setProviderUnavailText += " case " + uid.getUIDDefString() + " :
			// " + LF +
			// " providerServiceUID = " +
			// providerCompInst.getName().toUpperCase() + "_" +
			// serviceInst.getName().toUpperCase() + "_UID;" + LF +
			// " break;" + LF;
			// }
			// }
			// }

			setProviderUnavailText += "   }" + LF + LF +

					"   /* Set the provider unavailable */" + LF + "   " + pfServiceManName + "__Set_Availability(providerServiceUID, ECOA__FALSE);" + LF + LF +

					// TODO - amend logic for return status?!
					"   return ECOA__return_status_OK;" + LF + "}" + LF;
		}

		// Replace the #SET_PROVIDER_UNAVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_PROVIDER_UNAVAIL#", setProviderUnavailText);
	}

	public void writeSetServiceAvail() {
		String setServiceAvailText = SEP_PATTERN_71 + pfServiceManName + "__Set_Availability(ECOA__uint32 providedServiceUID, ECOA__boolean8 availability)";

		if (isHeader) {
			setServiceAvailText += ";" + LF + LF;
		} else {
			includeList.add("ECOA_time_utils");

			// Generate the set service availability function.
			setServiceAvailText += LF + "{" + LF + "   int servInst;" + LF + "   ECOA__timestamp opTimestamp;" + LF + "   ECOA_setTimestamp(&opTimestamp);" + LF + LF +

					"   /* Update the service list */" + LF + "   for (servInst = 0; servInst <= Provided_Services_MAXSIZE; servInst++)" + LF + "   {" + LF + "      if (" + lcp.getName() + "_serviceAvailabilityList[servInst].uid == providedServiceUID)" + LF + "      {" + LF + "         " + lcp.getName() + "_serviceAvailabilityList[servInst].available = availability;" + LF + "         break;" + LF +
					// TODO - remove this or amend logic...
					// " return ECOA__return_status_OK;" + LF +
					"      }" + LF + "   }" + LF + LF +
					// TODO - remove this or amend logic...
					// " /* Failed to find the service... */" + LF +
					// " return ECOA__return_status_OPERATION_NOT_AVAILABLE;" +
					// LF +

					"   /* Kick-off the notification process */" + LF + "   switch (providedServiceUID)" + LF + "   {" + LF;

			// TODO - need to forward this to correct PD or Platform?!
			// Generate a case for each provided service instance (unless no
			// requirers on the protection domain)
			for (SM_ComponentInstance compInst : pfManagerGenerator.getSystemModel().getFinalAssembly().getComponentInstances()) {
				// Only process provided services.
				for (SM_ServiceInstance serviceInstance : compInst.getCompType().getServiceInstancesList()) {
					setServiceAvailText += generateServiceAvailabilityCase(compInst, serviceInstance);
				}
			}

			setServiceAvailText += "   }" + LF + LF +

					"   unsigned char buffer[255];" + LF + "   int size;" + LF + LF +

					"   /* Log the Service Availability Change */" + LF + "   if (availability)" + LF + "   {" + LF + "      size = sprintf((char*)buffer, (char*)\"$3_%d:AVAILABLE\", providedServiceUID);" + LF + "   }" + LF + "   else" + LF + "   {" + LF + "      size = sprintf((char*)buffer, (char*)\"$3_%d:UNAVAILABLE\", providedServiceUID);" + LF + "   }" + LF + "   ecoaLog(buffer, size, LOG_LEVEL_CONTAINER_MONITOR, 0);" + LF + LF +

					// TODO - amend logic for return status?!
					"   return ECOA__return_status_OK;" + LF + "}" + LF;

		}

		// Replace the #SET_SERV_AVAIL# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SET_SERV_AVAIL#", setServiceAvailText);
	}

}

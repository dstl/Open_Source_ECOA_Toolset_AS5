/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.ECOA_System_Model.ImplLanguage;
import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.apigen.containerapi.ContainerWriter;
import com.iawg.ecoa.apigen.containerapi.ContainerWriterAda;
import com.iawg.ecoa.apigen.containerapi.ContainerWriterC;
import com.iawg.ecoa.apigen.containerapi.ContainerWriterCPP;
import com.iawg.ecoa.apigen.containertypes.ContainerTypesWriter;
import com.iawg.ecoa.apigen.containertypes.ContainerTypesWriterCPP;
import com.iawg.ecoa.apigen.containertypes.ContainerTypesWriterC;
import com.iawg.ecoa.apigen.containertypes.ContainerTypesWriterAda;
import com.iawg.ecoa.apigen.externalapi.ExternalWriter;
import com.iawg.ecoa.apigen.externalapi.ExternalWriterAda;
import com.iawg.ecoa.apigen.externalapi.ExternalWriterC;
import com.iawg.ecoa.apigen.externalapi.ExternalWriterCPP;
import com.iawg.ecoa.apigen.moduleapi.ModuleWriter;
import com.iawg.ecoa.apigen.moduleapi.ModuleWriterAda;
import com.iawg.ecoa.apigen.moduleapi.ModuleWriterC;
import com.iawg.ecoa.apigen.moduleapi.ModuleWriterCPP;
import com.iawg.ecoa.apigen.types.TypesWriter;
import com.iawg.ecoa.apigen.types.TypesWriterAda;
import com.iawg.ecoa.apigen.types.TypesWriterC;
import com.iawg.ecoa.apigen.types.TypesWriterCPP;
import com.iawg.ecoa.apigen.usercontext.UserContextWriter;
import com.iawg.ecoa.apigen.usercontext.UserContextWriterAda;
import com.iawg.ecoa.apigen.usercontext.UserContextWriterC;
import com.iawg.ecoa.apigen.usercontext.UserContextWriterCPP;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleType;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleTypeProperty;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_Pinfo;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataWrittenOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

public class APIGenerator {
	private static final Logger LOGGER = LogManager.getLogger(APIGenerator.class);
	private static final String SEP_PATTERN_01 = "header";
	private Path containerOutputDir;

	private SystemModel systemModel;

	private ToolConfig toolConfig;

	public APIGenerator(SystemModel systemModel, ToolConfig toolConfig, Path containerOutputDir) {
		this.containerOutputDir = containerOutputDir;
		this.systemModel = systemModel;
		this.toolConfig = toolConfig;

		LOGGER.info("ECOA API Code Generator Config:");
		LOGGER.info("| Overwrite Files                         - " + toolConfig.isOverwriteFiles());
		LOGGER.info("| Generate C Template Supervision Modules - " + toolConfig.isGenerateTemplateCModules());
		LOGGER.info("| Generate Body Stubs                     - " + toolConfig.generateBodies());
	}

	public void generate() {

		// //////////////////
		// Copy templates //
		// //////////////////
		WriterSupport.copyResource(containerOutputDir.resolve("include/ECOA.h"), "ECOA.h");

		WriterSupport.copyResource(containerOutputDir.resolve("include/ECOA.hpp"), "ECOA.hpp");

		WriterSupport.copyResource(containerOutputDir.resolve("include/ECOA.ads"), "ECOA.ads");

		// /////////////////////////////////////////
		// Write the container types header file //
		// /////////////////////////////////////////

		Map<String, SM_Namespace> namespaces = systemModel.getTypes().getNamespaces();
		Iterator<Entry<String, SM_Namespace>> it = namespaces.entrySet().iterator();

		while (it.hasNext()) {
			Entry<String, SM_Namespace> entry = it.next();
			// Don't write a file for ECOA namepace as done via template.
			if (!entry.getValue().getName().equals("ECOA")) {
				TypesWriterC typesWriterC = new TypesWriterC(containerOutputDir, entry.getValue());
				typesWriterC.generate();

				TypesWriterAda typesWriterAda = new TypesWriterAda(containerOutputDir, entry.getValue());
				typesWriterAda.generate();

				TypesWriter typesWriterCPP = new TypesWriterCPP(containerOutputDir, entry.getValue());
				typesWriterCPP.generate();
			}
		}

		// //////////////////////////////////////////////////
		// This is the start of the per module processing //
		// //////////////////////////////////////////////////

		Map<String, SM_ComponentImplementation> componentImplementations = systemModel.getComponentImplementations().getImplementations();
		Iterator<Entry<String, SM_ComponentImplementation>> itCompImpl = componentImplementations.entrySet().iterator();

		while (itCompImpl.hasNext()) {
			Entry<String, SM_ComponentImplementation> entryCompImpl = itCompImpl.next();
			SM_ComponentImplementation componentImplementation = entryCompImpl.getValue();

			Path componentDirName = componentImplementation.getContainingDir();

			for (SM_ModuleImpl moduleImplementation : componentImplementation.getModuleImplementations().values()) {

				ContainerWriter containerHeaderWrite = null;
				ContainerWriter containerBodyWrite = null;
				ModuleWriter moduleHeaderWrite = null;
				ModuleWriter moduleBodyWrite = null;
				UserContextWriter userContextWrite = null;
				ContainerTypesWriter containerTypesWrite = null;

				// Uncomment the following line and set the required language
				// to generate everything in that language - useful for testing
				// the
				// code generator!
				// moduleImplementation.setLanguage(ImplLanguage.ADA);

				Path moduleDir = componentDirName.resolve(moduleImplementation.getName());

				// Determine language of module implementation
				if (moduleImplementation.getLanguageType() == ImplLanguage.C) {
					containerHeaderWrite = new ContainerWriterC(systemModel, toolConfig, SEP_PATTERN_01, moduleDir, componentImplementation, moduleImplementation);
					containerBodyWrite = new ContainerWriterC(systemModel, toolConfig, "body", moduleDir, componentImplementation, moduleImplementation);
					moduleHeaderWrite = new ModuleWriterC(systemModel, toolConfig, SEP_PATTERN_01, moduleDir, componentImplementation, moduleImplementation);
					moduleBodyWrite = new ModuleWriterC(systemModel, toolConfig, "body", moduleDir, componentImplementation, moduleImplementation);
					userContextWrite = new UserContextWriterC(systemModel, toolConfig, moduleDir, moduleImplementation);
					containerTypesWrite = new ContainerTypesWriterC(systemModel, moduleDir, componentImplementation, moduleImplementation);
				} else if (moduleImplementation.getLanguageType() == ImplLanguage.ADA) {
					containerHeaderWrite = new ContainerWriterAda(systemModel, toolConfig, SEP_PATTERN_01, moduleDir, componentImplementation, moduleImplementation);
					containerBodyWrite = new ContainerWriterAda(systemModel, toolConfig, "body", moduleDir, componentImplementation, moduleImplementation);
					moduleHeaderWrite = new ModuleWriterAda(systemModel, toolConfig, SEP_PATTERN_01, moduleDir, componentImplementation, moduleImplementation);
					moduleBodyWrite = new ModuleWriterAda(systemModel, toolConfig, "body", moduleDir, componentImplementation, moduleImplementation);
					userContextWrite = new UserContextWriterAda(systemModel, toolConfig, moduleDir, moduleImplementation);
					containerTypesWrite = new ContainerTypesWriterAda(systemModel, moduleDir, componentImplementation, moduleImplementation);
				} else if (moduleImplementation.getLanguageType() == ImplLanguage.CPP) {
					containerHeaderWrite = new ContainerWriterCPP(systemModel, toolConfig, SEP_PATTERN_01, moduleDir, componentImplementation, moduleImplementation);
					containerBodyWrite = new ContainerWriterCPP(systemModel, toolConfig, "body", moduleDir, componentImplementation, moduleImplementation);
					moduleHeaderWrite = new ModuleWriterCPP(systemModel, toolConfig, SEP_PATTERN_01, moduleDir, componentImplementation, moduleImplementation);
					moduleBodyWrite = new ModuleWriterCPP(systemModel, toolConfig, "body", moduleDir, componentImplementation, moduleImplementation);
					userContextWrite = new UserContextWriterCPP(systemModel, toolConfig, moduleDir, moduleImplementation);
					containerTypesWrite = new ContainerTypesWriterCPP(systemModel, moduleDir, componentImplementation, moduleImplementation);
				}

				// Write the user context file
				writeUserContext(userContextWrite);

				// Write the module interface files
				writeModuleAPI(componentImplementation, moduleImplementation, moduleHeaderWrite, moduleBodyWrite);

				// Write the container interface files
				writeContainerAPI(componentImplementation, moduleImplementation, containerHeaderWrite, containerBodyWrite);

				// Write the container types file
				writeContainerTypes(containerTypesWrite, moduleImplementation.getModuleType());

			}

			// Write external interfaces
			writeExternalAPI(componentImplementation, componentDirName);
		}
	}

	private void writeUserContext(UserContextWriter userContextWrite) {
		userContextWrite.open();
		userContextWrite.writePreamble();
		userContextWrite.writeUserContext();
		userContextWrite.close();
	}

	private void writeContainerTypes(ContainerTypesWriter containerTypesWrite, SM_ModuleType modType) {
		containerTypesWrite.open();
		containerTypesWrite.writePreamble();

		for (SM_Object op : modType.getOperationList()) {
			// Versioned Data Read/Write Operations
			if (op instanceof SM_DataWrittenOp || op instanceof SM_DataReadOp) {
				containerTypesWrite.writeVDHandle(op.getName(), ((SM_DataOp) op).getData());
			}
		}

		if (modType.getIsSupervisor()) {
			// Generate any supervision module types (required/provided service
			// IDs).
			containerTypesWrite.writeSupervisionTypes();
		}

		containerTypesWrite.writeIncludes();

		containerTypesWrite.close();
	}

	private void writeContainerAPI(SM_ComponentImplementation componentImplementation, SM_ModuleImpl moduleImplementation, ContainerWriter containerHeaderWrite, ContainerWriter containerBodyWrite) {
		// ///////////////////////////////////////////
		// Start writing the container header file //
		// ///////////////////////////////////////////
		containerHeaderWrite.open();
		containerHeaderWrite.writePreamble();

		// /////////////////////////////////////////
		// Start writing the container body file //
		// /////////////////////////////////////////
		if (toolConfig.generateBodies()) {
			containerBodyWrite.open();
			containerBodyWrite.writePreamble();
		}

		SM_ModuleType moduleType = moduleImplementation.getModuleType();

		// /////////////////////////////////////////////////////
		// Processing logging and fault management operations //
		// /////////////////////////////////////////////////////
		containerHeaderWrite.writeLoggingServices();
		if (toolConfig.generateBodies()) {
			containerBodyWrite.writeLoggingServices();
		}
		// //////////////////////////////////////
		// Processing time services operations //
		// //////////////////////////////////////
		containerHeaderWrite.writeTimeServices();
		if (toolConfig.generateBodies()) {
			containerBodyWrite.writeTimeServices();
		}
		// /////////////////////////////////////////////////
		// Processing time resolution services operations //
		// /////////////////////////////////////////////////
		containerHeaderWrite.writeTimeResolutionServices();
		if (toolConfig.generateBodies()) {
			containerBodyWrite.writeTimeResolutionServices();
		}

		// /////////////////////////////////////////////////
		// Processing save non volatile context operation //
		// /////////////////////////////////////////////////
		containerHeaderWrite.writeSaveNonVolatileContext();
		if (toolConfig.generateBodies()) {
			containerBodyWrite.writeSaveNonVolatileContext();
		}

		// ////////////////////////////////////////
		// Processing module property operations //
		// ////////////////////////////////////////
		for (SM_ModuleTypeProperty property : moduleType.getModuleProperties()) {
			containerHeaderWrite.writeGetProperty(property);
			if (toolConfig.generateBodies()) {
				containerBodyWrite.writeGetProperty(property);
			}
		}

		for (SM_Object op : moduleType.getOperationList()) {
			// Request
			if (op instanceof SM_RequestSentOp) {
				SM_RequestSentOp requestSentOp = (SM_RequestSentOp) op;

				// Sync Request
				if (requestSentOp.getIsSynchronous()) {
					containerHeaderWrite.writeRequestSynchronous(requestSentOp.getName());
					if (toolConfig.generateBodies()) {
						containerBodyWrite.writeRequestSynchronous(requestSentOp.getName());
					}

					// Add parameters
					for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
						containerHeaderWrite.writeConstParameter(opParam);
						if (toolConfig.generateBodies()) {
							containerBodyWrite.writeConstParameter(opParam);
						}
					}
					for (SM_OperationParameter opParam : requestSentOp.getOutputs()) {
						containerHeaderWrite.writeParameter(opParam);
						if (toolConfig.generateBodies()) {
							containerBodyWrite.writeParameter(opParam);
						}
					}

					containerHeaderWrite.writeEndParameters();
					if (toolConfig.generateBodies()) {
						containerBodyWrite.writeEndParameters();
					}
				}
				// Async Request
				else {
					containerHeaderWrite.writeRequestAsynchronous(requestSentOp.getName());
					if (toolConfig.generateBodies()) {
						containerBodyWrite.writeRequestAsynchronous(requestSentOp.getName());
					}

					// Add parameters
					for (SM_OperationParameter opParam : requestSentOp.getInputs()) {
						containerHeaderWrite.writeConstParameter(opParam);
						if (toolConfig.generateBodies()) {
							containerBodyWrite.writeConstParameter(opParam);
						}
					}

					containerHeaderWrite.writeEndParameters();
					if (toolConfig.generateBodies()) {
						containerBodyWrite.writeEndParameters();
					}
				}
			}

			// Response Send
			if (op instanceof SM_RequestReceivedOp) {
				SM_RequestReceivedOp requestReceivedOp = (SM_RequestReceivedOp) op;

				// Write the response send container operation
				containerHeaderWrite.writeResponseSend(requestReceivedOp.getName());
				if (toolConfig.generateBodies()) {
					containerBodyWrite.writeResponseSend(requestReceivedOp.getName());
				}

				// Add out parameters
				for (SM_OperationParameter opParam : requestReceivedOp.getOutputs()) {
					containerHeaderWrite.writeConstParameter(opParam);
					if (toolConfig.generateBodies()) {
						containerBodyWrite.writeConstParameter(opParam);
					}
				}
				containerHeaderWrite.writeEndParameters();
				if (toolConfig.generateBodies()) {
					containerBodyWrite.writeEndParameters();
				}
			}

			// Versioned Data Read Operations
			if (op instanceof SM_DataReadOp) {
				SM_DataReadOp vdReadOp = (SM_DataReadOp) op;

				containerHeaderWrite.writeGetReadAccess(vdReadOp.getName());
				containerHeaderWrite.writeReleaseReadAccess(vdReadOp.getName());
				if (toolConfig.generateBodies()) {
					containerBodyWrite.writeGetReadAccess(vdReadOp.getName());
					containerBodyWrite.writeReleaseReadAccess(vdReadOp.getName());
				}
			}

			// Versioned Data Write Operations
			if (op instanceof SM_DataWrittenOp) {
				SM_DataWrittenOp vdWriteOp = (SM_DataWrittenOp) op;

				containerHeaderWrite.writeGetWriteAccess(vdWriteOp.getName());
				containerHeaderWrite.writeCancelWriteAccess(vdWriteOp.getName());
				containerHeaderWrite.writePublishWriteAccess(vdWriteOp.getName());
				if (toolConfig.generateBodies()) {
					containerBodyWrite.writeGetWriteAccess(vdWriteOp.getName());
					containerBodyWrite.writeCancelWriteAccess(vdWriteOp.getName());
					containerBodyWrite.writePublishWriteAccess(vdWriteOp.getName());
				}
			}

			// Sent Events
			if (op instanceof SM_EventSentOp) {
				SM_EventSentOp eventSentOp = (SM_EventSentOp) op;

				// This is an event the module sends so the signature will
				// appear
				// in the header (and body) for the container
				containerHeaderWrite.writeEventSend(eventSentOp.getName());
				if (toolConfig.generateBodies()) {
					containerBodyWrite.writeEventSend(eventSentOp.getName());
				}

				// Add parameters
				for (SM_OperationParameter opParam : eventSentOp.getInputs()) {
					containerHeaderWrite.writeConstParameter(opParam);
					if (toolConfig.generateBodies()) {
						containerBodyWrite.writeConstParameter(opParam);
					}
				}
				containerHeaderWrite.writeEndParametersNoStatusReturn();
				if (toolConfig.generateBodies()) {
					containerBodyWrite.writeEndParametersNoStatusReturn();
				}
			}
		}

		for (SM_Pinfo pinfo : moduleType.getPublicPinfos()) {
			containerHeaderWrite.writePInfo(pinfo.getName(), pinfo.isWriteable());
			if (toolConfig.generateBodies()) {
				containerBodyWrite.writePInfo(pinfo.getName(), pinfo.isWriteable());
			}
		}
		for (SM_Pinfo pinfo : moduleType.getPrivateReadPinfos()) {
			containerHeaderWrite.writePInfo(pinfo.getName(), pinfo.isWriteable());
			if (toolConfig.generateBodies()) {
				containerBodyWrite.writePInfo(pinfo.getName(), pinfo.isWriteable());
			}
		}
		for (SM_Pinfo pinfo : moduleType.getPrivateWritePinfos()) {
			containerHeaderWrite.writePInfo(pinfo.getName(), pinfo.isWriteable());
			if (toolConfig.generateBodies()) {
				containerBodyWrite.writePInfo(pinfo.getName(), pinfo.isWriteable());
			}
		}

		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Processing supervisor only management operations (lifecycle / service
		// availability / exception notification handler) //
		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (moduleType.getIsSupervisor()) {
			containerHeaderWrite.writeGetRequiredAvailability();
			if (toolConfig.generateBodies()) {
				containerBodyWrite.writeGetRequiredAvailability();
			}
			containerHeaderWrite.writeSetProvidedAvailability();
			if (toolConfig.generateBodies()) {
				containerBodyWrite.writeSetProvidedAvailability();
			}

			// Lifecycle operations
			containerHeaderWrite.writeLifecycleServices();
			if (toolConfig.generateBodies()) {
				containerBodyWrite.writeLifecycleServices();
			}

			if (moduleType.getIsFaultHandler()) {
				// Error Handler operation
				containerHeaderWrite.writeRecoveryAction();
				if (toolConfig.generateBodies()) {
					containerBodyWrite.writeRecoveryAction();
				}
			}

		}

		// Close the files
		containerHeaderWrite.close();
		if (toolConfig.generateBodies()) {
			containerBodyWrite.close();
		}
	}

	private void writeExternalAPI(SM_ComponentImplementation componentImplementation, Path componentDirName) {
		ExternalWriter externalCWrite = null;
		ExternalWriter externalCPPWrite = null;
		ExternalWriter externalAdaWrite = null;

		for (SM_SenderExternal externalSender : componentImplementation.getExternalSenders()) {
			// Get the parameters (need to look at a receiver in order to get
			// them!)
			SM_ReceiverInterface eventRx = externalSender.getParentLink().getReceivers().get(0);
			List<SM_OperationParameter> params = eventRx.getInputs();

			if (externalSender.getLanguage().equals("C")) {
				// Create new writer if first time.
				if (externalCWrite == null) {
					externalCWrite = new ExternalWriterC(componentDirName, componentImplementation);
					externalCWrite.open();
					externalCWrite.writePreamble();
				}
				externalCWrite.writeExternalInterface(externalSender.getSenderOpName(), params);
			}

			if (externalSender.getLanguage().equals("C++")) {
				// Create new writer if first time.
				if (externalCPPWrite == null) {
					externalCPPWrite = new ExternalWriterCPP(componentDirName, componentImplementation);
					externalCPPWrite.open();
					externalCPPWrite.writePreamble();
				}
				externalCPPWrite.writeExternalInterface(externalSender.getSenderOpName(), params);
			}

			if (externalSender.getLanguage().equals("Ada")) {
				// Create new writer if first time.
				if (externalAdaWrite == null) {
					externalAdaWrite = new ExternalWriterAda(componentDirName, componentImplementation);
					externalAdaWrite.open();
					externalAdaWrite.writePreamble();
				}
				externalAdaWrite.writeExternalInterface(externalSender.getSenderOpName(), params);
			}
		}

		// Close the writers (if opened).
		if (externalCWrite != null) {
			externalCWrite.close();
		}
		if (externalCPPWrite != null) {
			externalCPPWrite.close();
		}
		if (externalAdaWrite != null) {
			externalAdaWrite.close();
		}
	}

	private void writeModuleAPI(SM_ComponentImplementation componentImplementation, SM_ModuleImpl moduleImplementation, ModuleWriter moduleHeaderWrite, ModuleWriter moduleBodyWrite) {
		// ////////////////////////////////////////
		// Start writing the module header file //
		// ////////////////////////////////////////
		moduleHeaderWrite.open();
		moduleHeaderWrite.writePreamble();

		// /////////////////////////////////////////
		// Start writing the module body file //
		// /////////////////////////////////////////
		if (toolConfig.generateBodies()) {
			;
			moduleBodyWrite.open();
			moduleBodyWrite.writePreamble();
		}

		SM_ModuleType moduleType = moduleImplementation.getModuleType();

		// //////////////////////////////////////
		// Process Module Lifecycle Operations //
		// //////////////////////////////////////
		moduleHeaderWrite.writeLifecycleServices();
		if (toolConfig.generateBodies()) {
			moduleBodyWrite.writeLifecycleServices();
		}

		// ////////////////////////////////////////////////////////////
		// Process Operations (operation list is ordered as per XML) //
		// ////////////////////////////////////////////////////////////
		for (SM_Object op : moduleType.getOperationList()) {
			// Event Received
			if (op instanceof SM_EventReceivedOp) {
				SM_EventReceivedOp eventRxOp = (SM_EventReceivedOp) op;

				// This is an event the module receives so the signature will
				// appear
				// in the header (and body) for the module
				moduleHeaderWrite.writeEventReceived(eventRxOp);
				if (toolConfig.generateBodies()) {
					moduleBodyWrite.writeEventReceived(eventRxOp);
				}
			}

			// Request Received
			if (op instanceof SM_RequestReceivedOp) {
				SM_RequestReceivedOp requestReceivedOp = (SM_RequestReceivedOp) op;

				// Write the request received module operation
				moduleHeaderWrite.writeRequestReceived(requestReceivedOp.getName());
				if (toolConfig.generateBodies()) {
					moduleBodyWrite.writeRequestReceived(requestReceivedOp.getName());
				}

				// Add parameters
				for (SM_OperationParameter opParam : requestReceivedOp.getInputs()) {
					moduleHeaderWrite.writeConstParameter(opParam);
					if (toolConfig.generateBodies()) {
						moduleBodyWrite.writeConstParameter(opParam);
					}
				}
				moduleHeaderWrite.writeEndParameters();
				if (toolConfig.generateBodies()) {
					moduleBodyWrite.writeEndParameters();
				}
			}

			// Request Sent (async)
			if (op instanceof SM_RequestSentOp) {
				SM_RequestSentOp requestSentOp = (SM_RequestSentOp) op;

				if (!requestSentOp.getIsSynchronous()) {
					moduleHeaderWrite.writeResponseReceivedAsynchonous(requestSentOp.getName());
					if (toolConfig.generateBodies()) {
						moduleBodyWrite.writeResponseReceivedAsynchonous(requestSentOp.getName());
					}
				}

				// Add parameters
				for (SM_OperationParameter opParam : requestSentOp.getOutputs()) {
					if (!requestSentOp.getIsSynchronous()) {
						// asynchronous outputs are actually inputs to the
						// function, so should be const
						moduleHeaderWrite.writeConstParameter(opParam);
						if (toolConfig.generateBodies()) {
							moduleBodyWrite.writeConstParameter(opParam);
						}
					}
				}

				if (!requestSentOp.getIsSynchronous()) {
					moduleHeaderWrite.writeEndParameters();
					if (toolConfig.generateBodies()) {
						moduleBodyWrite.writeEndParameters();
					}
				}
			}

			// Versioned Data Read Operations (Notifying)
			if (op instanceof SM_DataReadOp) {
				SM_DataReadOp vdReadOp = (SM_DataReadOp) op;
				// Notifying reads
				if (vdReadOp.getIsNotifying()) {
					moduleHeaderWrite.writeVDUpdated(vdReadOp.getName());
					if (toolConfig.generateBodies()) {
						moduleBodyWrite.writeVDUpdated(vdReadOp.getName());
					}
				}
			}
		}

		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Processing supervisor only management operations (lifecycle / service
		// availability / exception notification handler) //
		// ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		if (moduleType.getIsSupervisor()) {

			// Service availability operations (module API)
			moduleHeaderWrite.writeServiceAvailabilityNotifications();
			if (toolConfig.generateBodies()) {
				moduleBodyWrite.writeServiceAvailabilityNotifications();
			}

			for (SM_ModuleInstance moduleInstance : componentImplementation.getModuleInstances().values()) {
				// Only do this if it's not a supervisor
				if (!moduleInstance.getImplementation().getModuleType().getIsSupervisor()) {

					// Lifecycle operations
					moduleHeaderWrite.writeLifecycleNotification(moduleInstance);
					if (toolConfig.generateBodies()) {
						moduleBodyWrite.writeLifecycleNotification(moduleInstance);
					}

					// Error notification handler
					moduleHeaderWrite.writeErrorNotification(moduleInstance);
					if (toolConfig.generateBodies()) {
						moduleBodyWrite.writeErrorNotification(moduleInstance);
					}

				}

			}

			for (SM_TriggerInstance triggerInstance : componentImplementation.getTriggerInstances().values()) {

				// Lifecycle operations
				moduleHeaderWrite.writeLifecycleNotification(triggerInstance);
				if (toolConfig.generateBodies()) {
					moduleBodyWrite.writeLifecycleNotification(triggerInstance);
				}

			}

			for (SM_DynamicTriggerInstance dynamicTriggerInstance : componentImplementation.getDynamicTriggerInstances().values()) {

				// Lifecycle operations
				moduleHeaderWrite.writeLifecycleNotification(dynamicTriggerInstance);
				if (toolConfig.generateBodies()) {
					moduleBodyWrite.writeLifecycleNotification(dynamicTriggerInstance);
				}

			}

		}

		if (moduleType.getIsFaultHandler()) {
			// Error Handler operation
			moduleHeaderWrite.writeFaultHandlerNotification();
			if (toolConfig.generateBodies()) {
				moduleBodyWrite.writeFaultHandlerNotification();
			}
		}

		// Close the files
		moduleHeaderWrite.close();
		if (toolConfig.generateBodies()) {
			moduleBodyWrite.close();
		}

	}

}

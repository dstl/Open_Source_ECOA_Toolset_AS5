/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import tech.ecoa.osets.eclipse.plugin.common.Constants;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ComponentPropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.CompositePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ReferenceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ServiceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceTerminalNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Enums;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Enums.ModuleOperationTypes;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstancePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationParameterNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceTerminalNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ComputingNodeConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeploymentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingPlatformNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalProcessorsNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;
import tech.ecoa.osets.model.cdef.Component;
import tech.ecoa.osets.model.cdef.ComponentReference;
import tech.ecoa.osets.model.cdef.ComponentService;
import tech.ecoa.osets.model.cdef.Composite;
import tech.ecoa.osets.model.cdef.Instance;
import tech.ecoa.osets.model.cdef.Instance.Implementation;
import tech.ecoa.osets.model.cdef.ObjectFactory;
import tech.ecoa.osets.model.cdef.Property;
import tech.ecoa.osets.model.cdef.PropertyValue;
import tech.ecoa.osets.model.cdef.ValueType;
import tech.ecoa.osets.model.cdef.Wire;
import tech.ecoa.osets.model.cimp.ComponentImplementation;
import tech.ecoa.osets.model.cimp.DataLink;
import tech.ecoa.osets.model.cimp.DynamicTriggerInstance;
import tech.ecoa.osets.model.cimp.Event;
import tech.ecoa.osets.model.cimp.EventLink;
import tech.ecoa.osets.model.cimp.ModuleImplementation;
import tech.ecoa.osets.model.cimp.ModuleInstance;
import tech.ecoa.osets.model.cimp.ModuleType;
import tech.ecoa.osets.model.cimp.ModuleType.Operations.DataRead;
import tech.ecoa.osets.model.cimp.ModuleType.Operations.EventReceived;
import tech.ecoa.osets.model.cimp.ModuleType.Operations.RequestReceived;
import tech.ecoa.osets.model.cimp.ModuleType.Operations.RequestSent;
import tech.ecoa.osets.model.cimp.ModuleType.Properties;
import tech.ecoa.osets.model.cimp.OpRef;
import tech.ecoa.osets.model.cimp.OpRefActivatable;
import tech.ecoa.osets.model.cimp.OpRefActivatableFifo;
import tech.ecoa.osets.model.cimp.OpRefActivatingFifo;
import tech.ecoa.osets.model.cimp.OpRefServer;
import tech.ecoa.osets.model.cimp.OpRefTrigger;
import tech.ecoa.osets.model.cimp.Parameter;
import tech.ecoa.osets.model.cimp.PropertyValues;
import tech.ecoa.osets.model.cimp.RequestLink;
import tech.ecoa.osets.model.cimp.TriggerInstance;
import tech.ecoa.osets.model.cimp.Use;
import tech.ecoa.osets.model.cimp.VersionedData;
import tech.ecoa.osets.model.deploy.ComputingNodeConfiguration;
import tech.ecoa.osets.model.deploy.Deployment;
import tech.ecoa.osets.model.deploy.PlatformConfiguration;
import tech.ecoa.osets.model.deploy.ProtectionDomain;
import tech.ecoa.osets.model.deploy.ProtectionDomain.DeployedModuleInstance;
import tech.ecoa.osets.model.deploy.ProtectionDomain.DeployedTriggerInstance;
import tech.ecoa.osets.model.deploy.ProtectionDomain.ExecuteOn;
import tech.ecoa.osets.model.lsys.LogicalSystem;
import tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform;
import tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode.AvailableMemory;
import tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode.Endianess;
import tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode.LogicalProcessors;
import tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode.LogicalProcessors.StepDuration;
import tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode.ModuleSwitchTime;
import tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode.Os;

@SuppressWarnings("deprecation")
public class ExportUtil {
	public static Composite getInitialAssemblyJAXBFromNode(CompositeNode node) {
		Composite ret = new Composite();
		ret.setName(node.getName());
		ret.setTargetNamespace("http://www.ecoa.technology/sca");
		for (Node cNode : node.getChild()) {
			if (cNode instanceof CompositePropertyNode) {
				CompositePropertyNode val = (CompositePropertyNode) cNode;
				Property prop = new Property();
				prop.setName(val.getName());
				prop.setType(Constants.XS_QNAME);
				prop.getOtherAttributes().put(Constants.TYPE_QNAME, val.getType());
				if (val.getValue() != null) {
					ValueType propVal = new ValueType();
					propVal.getContent().clear();
					prop.getContent().clear();
					propVal.getContent().add(val.getValue());
					prop.getContent().add(propVal);
				}
				ret.getServiceOrPropertyOrComponent().add(prop);
			} else if (cNode instanceof ComponentNode) {
				ComponentNode val = (ComponentNode) cNode;
				Component comp = new Component();
				comp.setName(val.getName());
				Instance inst = new Instance();
				inst.setComponentType(val.getType());
				comp.setImplementation((new ObjectFactory()).createInstance(inst));
				for (Node gNode : cNode.getChild()) {
					if (gNode instanceof ComponentPropertyNode) {
						ComponentPropertyNode pVal = (ComponentPropertyNode) gNode;
						PropertyValue prop = new PropertyValue();
						ValueType propVal = new ValueType();
						prop.setName(pVal.getName());
						prop.setType(Constants.XS_QNAME);
						prop.getOtherAttributes().put(Constants.TYPE_QNAME, pVal.getType());
						propVal.getContent().clear();
						prop.getContent().clear();
						propVal.getContent().add(pVal.getValue());
						prop.getContent().add(propVal);
						comp.getServiceOrReferenceOrProperty().add(prop);
					} else if (gNode instanceof ReferenceNode) {
						ReferenceNode rVal = (ReferenceNode) gNode;
						ComponentReference ref = new ComponentReference();
						ref.setName(rVal.getName());
						comp.getServiceOrReferenceOrProperty().add(ref);
					} else if (gNode instanceof ServiceNode) {
						ServiceNode sVal = (ServiceNode) gNode;
						ComponentService srvc = new ComponentService();
						srvc.setName(sVal.getName());
						comp.getServiceOrReferenceOrProperty().add(srvc);
					}
				}
				ret.getServiceOrPropertyOrComponent().add(comp);
			}
		}
		for (Link link : node.getLinks()) {
			ServiceNode src = (ServiceNode) link.getSource();
			ReferenceNode trg = (ReferenceNode) link.getTarget();
			String srcVal = ((ComponentNode) src.getParent()).getName().trim() + "/" + src.getName().trim();
			String trgVal = ((ComponentNode) trg.getParent()).getName().trim() + "/" + trg.getName().trim();
			String rank = link.getRank();
			Wire wire = new Wire();
			/* Fix for incorrect references */
			wire.setSource(trgVal);
			wire.setTarget(srcVal);
			wire.setRank(new BigInteger(rank));
			ret.getServiceOrPropertyOrComponent().add(wire);
		}
		return ret;
	}

	public static Composite getFinalAssemblyJAXBFromNode(tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode node) {
		Composite ret = new Composite();
		ret.setName(node.getName());
		ret.setTargetNamespace("http://www.ecoa.technology/sca");
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node cNode : node.getChild()) {
			if (cNode instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode) {
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode val = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode) cNode;
				Property prop = new Property();
				prop.setName(val.getName());
				prop.setType(Constants.XS_QNAME);
				prop.getOtherAttributes().put(Constants.TYPE_QNAME, val.getType());
				if (val.getValue() != null) {
					ValueType propVal = new ValueType();
					propVal.getContent().clear();
					prop.getContent().clear();
					propVal.getContent().add(val.getValue());
					prop.getContent().add(propVal);
				}
				ret.getServiceOrPropertyOrComponent().add(prop);
			} else if (cNode instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode) {
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode val = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode) cNode;
				Component comp = new Component();
				comp.setName(val.getName());
				Instance inst = new Instance();
				Implementation impl = new Implementation();
				impl.setName(val.getInst());
				inst.setImplementation(impl);
				inst.setComponentType(val.getType());
				comp.setImplementation((new ObjectFactory()).createInstance(inst));
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node gNode : cNode.getChild()) {
					if (gNode instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode pVal = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode) gNode;
						PropertyValue prop = new PropertyValue();
						ValueType propVal = new ValueType();
						prop.setName(pVal.getName());
						prop.setType(Constants.XS_QNAME);
						prop.getOtherAttributes().put(Constants.TYPE_QNAME, pVal.getType());
						propVal.getContent().clear();
						prop.getContent().clear();
						propVal.getContent().add(pVal.getValue());
						prop.getContent().add(propVal);
						comp.getServiceOrReferenceOrProperty().add(prop);
					} else if (gNode instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode rVal = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode) gNode;
						ComponentReference ref = new ComponentReference();
						ref.setName(rVal.getName());
						comp.getServiceOrReferenceOrProperty().add(ref);
					} else if (gNode instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode sVal = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode) gNode;
						ComponentService srvc = new ComponentService();
						srvc.setName(sVal.getName());
						comp.getServiceOrReferenceOrProperty().add(srvc);
					}
				}
				ret.getServiceOrPropertyOrComponent().add(comp);
			}
		}
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link link : node.getLinks()) {
			tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode src = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode) link.getSource();
			tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode trg = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode) link.getTarget();
			String srcVal = ((tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode) src.getParent()).getName().trim() + "/" + src.getName().trim();
			String trgVal = ((tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode) trg.getParent()).getName().trim() + "/" + trg.getName().trim();
			String rank = link.getRank();
			Wire wire = new Wire();
			/* Fix for incorrect references */
			wire.setSource(trgVal);
			wire.setTarget(srcVal);
			wire.setRank(new BigInteger(rank));
			ret.getServiceOrPropertyOrComponent().add(wire);
		}
		return ret;
	}

	public static LogicalSystem getLogicalSystemJAXBFromNode(LogicalSystemNode node) {
		LogicalSystem ret = new LogicalSystem();
		ret.setId(node.getName());
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node cNode : node.getChild()) {
			LogicalComputingPlatformNode val = (LogicalComputingPlatformNode) cNode;
			LogicalComputingPlatform plat = new LogicalComputingPlatform();
			plat.setId(val.getName());
			for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node gNode : cNode.getChild()) {
				LogicalComputingNode nVal = (LogicalComputingNode) gNode;
				tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode ref = new tech.ecoa.osets.model.lsys.LogicalSystem.LogicalComputingPlatform.LogicalComputingNode();
				ref.setId(nVal.getName());
				Endianess end = new Endianess();
				end.setType(nVal.getEndianess());
				ref.setEndianess(end);
				AvailableMemory aMem = new AvailableMemory();
				aMem.setGigaBytes(new BigInteger(nVal.getAvailMem()));
				ref.setAvailableMemory(aMem);
				ModuleSwitchTime sTime = new ModuleSwitchTime();
				sTime.setMicroSeconds(new BigInteger(nVal.getMst()));
				ref.setModuleSwitchTime(sTime);
				Os os = new Os();
				os.setName(nVal.getOsName());
				os.setVersion(nVal.getOsVer());
				ref.setOs(os);
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node gcNode : gNode.getChild()) {
					LogicalProcessorsNode gcVal = (LogicalProcessorsNode) gcNode;
					LogicalProcessors proc = new LogicalProcessors();
					proc.setNumber(new BigInteger(gcVal.getNum()));
					StepDuration dur = new StepDuration();
					dur.setNanoSeconds(new BigInteger(gcVal.getStepDur()));
					proc.setStepDuration(dur);
					proc.setType(gcVal.getType());
					ref.getLogicalProcessors().add(proc);
				}
				plat.getLogicalComputingNode().add(ref);
			}
			ret.getLogicalComputingPlatform().add(plat);
		}
		return ret;
	}

	public static Deployment getDeploymentJAXBFromNode(DeploymentNode node) {
		Deployment ret = new Deployment();
		ret.setFinalAssembly(node.getfAssmbl());
		ret.setLogicalSystem(node.getlSys());
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node cNode : node.getChild()) {
			if (cNode instanceof PlatformConfigurationNode) {
				PlatformConfigurationNode cVal = (PlatformConfigurationNode) cNode;
				PlatformConfiguration config = new PlatformConfiguration();
				config.setComputingPlatform(cVal.getCompPlatform());
				config.setNotificationMaxNumber(new BigDecimal(cVal.getNotifMaxNumber()));
				ret.getPlatformConfiguration().add(config);
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node gNode : cNode.getChild()) {
					if (gNode instanceof ComputingNodeConfigurationNode) {
						ComputingNodeConfigurationNode cncNode = (ComputingNodeConfigurationNode) gNode;
						ComputingNodeConfiguration cnc = new ComputingNodeConfiguration();
						cnc.setComputingNode(cncNode.getName());
						cnc.setSchedulingInformation(cncNode.getSchedInfo());
						config.getComputingNodeConfiguration().add(cnc);
					}
				}
			} else if (cNode instanceof ProtectionDomainNode) {
				ProtectionDomainNode dNode = (ProtectionDomainNode) cNode;
				ProtectionDomain pDom = new ProtectionDomain();
				pDom.setName(dNode.getName());
				ExecuteOn eo = new ExecuteOn();
				eo.setComputingNode(dNode.getEoCompNode());
				eo.setComputingPlatform(dNode.getEoCompPlatform());
				pDom.setExecuteOn(eo);
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node gNode : cNode.getChild()) {
					if (gNode instanceof DeployedTriggerInstanceNode) {
						DeployedTriggerInstanceNode diNode = (DeployedTriggerInstanceNode) gNode;
						DeployedTriggerInstance dInst = new DeployedTriggerInstance();
						dInst.setComponentName(diNode.getCompName());
						dInst.setTriggerInstanceName(diNode.getTriggerName());
						dInst.setTriggerPriority(new BigDecimal(diNode.getPriority()));
						pDom.getDeployedModuleInstanceOrDeployedTriggerInstance().add(dInst);
					} else if (gNode instanceof DeployedModuleInstanceNode) {
						DeployedModuleInstanceNode dmNode = (DeployedModuleInstanceNode) gNode;
						DeployedModuleInstance mInst = new DeployedModuleInstance();
						mInst.setComponentName(dmNode.getCompName());
						mInst.setModuleInstanceName(dmNode.getModuleName());
						mInst.setModulePriority(new BigDecimal(dmNode.getPriority()));
						pDom.getDeployedModuleInstanceOrDeployedTriggerInstance().add(mInst);
					}
				}
				ret.getProtectionDomain().add(pDom);
			}
		}
		return ret;
	}

	public static ComponentImplementation getComponentImplementationJAXBFromNode(ComponentImplementationNode node) {
		try {
			ComponentImplementation cImp = new ComponentImplementation();
			cImp.setComponentDefinition(node.getName());
			tech.ecoa.osets.model.cimp.ObjectFactory factory = new tech.ecoa.osets.model.cimp.ObjectFactory();
			ArrayList<Use> use = new ArrayList<Use>();
			for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node cNd : node.getChild()) {
				if (cNd instanceof ModuleTypeNode) {
					ModuleTypeNode cNode = (ModuleTypeNode) cNd;
					ModuleType mType = factory.createModuleType();
					mType.setIsSupervisionModule(cNode.isSup());
					mType.setName(cNode.getName());
					mType.setOperations(factory.createModuleTypeOperations());
					for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node pNd : cNode.getChild()) {
						if (pNd instanceof ModuleImplementationNode) {
							ModuleImplementationNode pNode = (ModuleImplementationNode) pNd;
							ModuleImplementation mImp = factory.createModuleImplementation();
							mImp.setLanguage(pNode.getLang());
							mImp.setModuleType(pNode.getType());
							mImp.setName(pNode.getName());
							cImp.getModuleImplementation().add(mImp);
						} else if (pNd instanceof ModuleInstanceNode) {
							ModuleInstanceNode pNode = (ModuleInstanceNode) pNd;
							ModuleInstance mInst = factory.createModuleInstance();
							mInst.setImplementationName(pNode.getImpl());
							mInst.setName(pNode.getName());
							BigDecimal bd = new BigDecimal(pNode.getPriority());
							mInst.setRelativePriority(bd.intValue());
							PropertyValues vals = mInst.getPropertyValues();
							for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node gNd : pNode.getChild()) {
								ModuleInstancePropertyNode gNode = (ModuleInstancePropertyNode) gNd;
								if (mType.getProperties() == null)
									mType.setProperties(factory.createModuleTypeProperties());
								if (!contains(gNode.getName(), mType.getProperties())) {
									Parameter param = new Parameter();
									param.setName(gNode.getName());
									param.setType(gNode.getType());
									mType.getProperties().getProperty().add(param);
								}
								if (vals == null)
									vals = new PropertyValues();
								tech.ecoa.osets.model.cimp.PropertyValue val = new tech.ecoa.osets.model.cimp.PropertyValue();
								val.setName(gNode.getName());
								val.setValue(gNode.getValue());
								vals.getPropertyValue().add(val);
							}
							mInst.setPropertyValues(vals);
							cImp.getModuleInstance().add(mInst);
						} else if (pNd instanceof ModuleOperationNode) {
							ModuleOperationNode pNode = (ModuleOperationNode) pNd;
							switch (Enums.getModuleOperationType(pNode.getType())) {
							case DATA_READ:
								DataRead dR = factory.createModuleTypeOperationsDataRead();
								dR.setName(pNode.getName());
								dR.setType(pNode.getdType());
								mType.getOperations().getDataWrittenOrDataReadOrEventSent().add(dR);
								break;
							case DATA_WRITE:
								VersionedData dW = new VersionedData();
								dW.setName(pNode.getName());
								dW.setType(pNode.getdType());
								mType.getOperations().getDataWrittenOrDataReadOrEventSent().add(dW);
								break;
							case EVENT_RECEIVED:
								EventReceived eR = factory.createModuleTypeOperationsEventReceived();
								eR.setName(pNode.getName());
								populateOpParam(eR, pNode, Enums.getModuleOperationType(pNode.getType()), use);
								mType.getOperations().getDataWrittenOrDataReadOrEventSent().add(eR);
								break;
							case EVENT_SENT:
								Event e = new Event();
								e.setName(pNode.getName());
								populateOpParam(e, pNode, Enums.getModuleOperationType(pNode.getType()), use);
								mType.getOperations().getDataWrittenOrDataReadOrEventSent().add(e);
								break;
							case REQUEST_RECEIVED:
								RequestReceived rR = factory.createModuleTypeOperationsRequestReceived();
								rR.setName(pNode.getName());
								populateOpParam(rR, pNode, Enums.getModuleOperationType(pNode.getType()), use);
								mType.getOperations().getDataWrittenOrDataReadOrEventSent().add(rR);
								break;
							case REQUEST_SENT:
								RequestSent rS = factory.createModuleTypeOperationsRequestSent();
								rS.setName(pNode.getName());
								rS.setIsSynchronous(pNode.isSync());
								rS.setTimeout(Double.parseDouble(pNode.getTimeout()));
								populateOpParam(rS, pNode, Enums.getModuleOperationType(pNode.getType()), use);
								mType.getOperations().getDataWrittenOrDataReadOrEventSent().add(rS);
								break;
							}
						}
					}
					cImp.getModuleType().add(mType);
				} else if (cNd instanceof TriggerInstanceNode) {
					TriggerInstanceNode cNode = (TriggerInstanceNode) cNd;
					TriggerInstance tInst = factory.createTriggerInstance();
					tInst.setName(cNode.getName());
					BigDecimal bd = new BigDecimal(cNode.getPriority());
					tInst.setRelativePriority(bd.intValue());
					cImp.getTriggerInstance().add(tInst);
				} else if (cNd instanceof DynamicTriggerInstanceNode) {
					DynamicTriggerInstanceNode cNode = (DynamicTriggerInstanceNode) cNd;
					DynamicTriggerInstance dtInst = factory.createDynamicTriggerInstance();
					dtInst.setName(cNode.getName());
					BigDecimal bd = new BigDecimal(cNode.getPriority());
					dtInst.setRelativePriority(bd.intValue());
					cImp.getDynamicTriggerInstance().add(dtInst);
				}
			}
			cImp.getUse().addAll(use);
			for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link link : node.getLinks()) {
				switch (Enums.getLinkType(link.getType())) {
				case DATA:
					DataLink dL = factory.createDataLink();
					dL.setWriters(factory.createDataLinkWriters());
					if (link.getSource() instanceof ModuleOperationNode) {
						ModuleOperationNode nd = (ModuleOperationNode) link.getSource();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(link.getsInst());
						ref.setOperationName(nd.getName());
						dL.getWriters().getReferenceOrModuleInstance().add(factory.createDataLinkWritersModuleInstance(ref));
					} else if (link.getSource() instanceof ServiceOperationNode) {
						ServiceOperationNode nd = (ServiceOperationNode) link.getSource();
						tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode par = (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) nd.getParent();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(par.getName());
						ref.setOperationName(nd.getName());
						dL.getWriters().getReferenceOrModuleInstance().add(factory.createDataLinkWritersReference(ref));
					}
					cImp.getDataLinkOrEventLinkOrRequestLink().add(dL);
					dL.setReaders(factory.createDataLinkReaders());
					if (link.getTarget() instanceof ModuleOperationNode) {
						ModuleOperationNode nd = (ModuleOperationNode) link.getTarget();
						OpRefActivatableFifo ref = factory.createOpRefActivatableFifo();
						ref.setInstanceName(link.gettInst());
						ref.setOperationName(nd.getName());
						dL.getReaders().getServiceOrModuleInstance().add(ref);
					} else if (link.getTarget() instanceof ServiceOperationNode) {
						ServiceOperationNode nd = (ServiceOperationNode) link.getTarget();
						tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode par = (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) nd.getParent();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(par.getName());
						ref.setOperationName(nd.getName());
						dL.getReaders().getServiceOrModuleInstance().add(ref);
					}
					break;
				case EVENT:
					EventLink eL = factory.createEventLink();
					eL.setSenders(factory.createEventLinkSenders());
					if (link.getSource() instanceof ModuleOperationNode) {
						ModuleOperationNode nd = (ModuleOperationNode) link.getSource();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(link.getsInst());
						ref.setOperationName(nd.getName());
						eL.getSenders().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkSendersModuleInstance(ref));
					} else if (link.getSource() instanceof ServiceOperationNode) {
						ServiceOperationNode nd = (ServiceOperationNode) link.getSource();
						tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode par = (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) nd.getParent();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(par.getName());
						ref.setOperationName(nd.getName());
						if (par.getType().equalsIgnoreCase(Enums.ServiceTypes.PROVIDED.name()))
							eL.getSenders().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkSendersService(ref));
						else
							eL.getSenders().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkSendersReference(ref));
					} else if (link.getSource() instanceof TriggerInstanceTerminalNode) {
						TriggerInstanceTerminalNode nd = (TriggerInstanceTerminalNode) link.getSource();
						OpRefTrigger trg = factory.createOpRefTrigger();
						trg.setInstanceName(((TriggerInstanceNode) nd.getParent()).getName());
						if (link.getPeriod() != null && NumberUtils.isNumber(link.getPeriod()))
							trg.setPeriod(Double.parseDouble(link.getPeriod()));
						eL.getSenders().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkSendersTrigger(trg));
					} else if (link.getSource() instanceof DynamicTriggerInstanceTerminalNode) {
						DynamicTriggerInstanceTerminalNode nd = (DynamicTriggerInstanceTerminalNode) link.getSource();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(((DynamicTriggerInstanceNode) nd.getParent()).getName());
						ref.setOperationName(nd.getName().toLowerCase());
						eL.getSenders().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkSendersDynamicTrigger(ref));
					}
					eL.setReceivers(factory.createEventLinkReceivers());
					if (link.getTarget() instanceof ModuleOperationNode) {
						ModuleOperationNode nd = (ModuleOperationNode) link.getTarget();
						OpRefActivatableFifo ref = factory.createOpRefActivatableFifo();
						ref.setInstanceName(link.gettInst());
						ref.setOperationName(nd.getName());
						eL.getReceivers().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkReceiversModuleInstance(ref));
					} else if (link.getTarget() instanceof ServiceOperationNode) {
						ServiceOperationNode nd = (ServiceOperationNode) link.getTarget();
						tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode par = (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) nd.getParent();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(par.getName());
						ref.setOperationName(nd.getName());
						if (par.getType().equalsIgnoreCase(Enums.ServiceTypes.PROVIDED.name()))
							eL.getReceivers().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkReceiversService(ref));
						else
							eL.getReceivers().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkReceiversReference(ref));
					} else if (link.getTarget() instanceof DynamicTriggerInstanceTerminalNode) {
						DynamicTriggerInstanceTerminalNode nd = (DynamicTriggerInstanceTerminalNode) link.getTarget();
						OpRefActivatingFifo ref = factory.createOpRefActivatingFifo();
						ref.setInstanceName(((DynamicTriggerInstanceNode) nd.getParent()).getName());
						ref.setOperationName(nd.getName().toLowerCase());
						eL.getReceivers().getServiceOrReferenceOrModuleInstance().add(factory.createEventLinkReceiversDynamicTrigger(ref));
					}
					cImp.getDataLinkOrEventLinkOrRequestLink().add(eL);
					break;
				case REQUEST:
					RequestLink rL = factory.createRequestLink();
					rL.setClients(factory.createRequestLinkClients());
					if (link.getSource() instanceof ModuleOperationNode) {
						ModuleOperationNode nd = (ModuleOperationNode) link.getSource();
						OpRefActivatable ref = factory.createOpRefActivatable();
						ref.setInstanceName(link.getsInst());
						ref.setOperationName(nd.getName());
						rL.getClients().getServiceOrModuleInstance().add(ref);
					} else if (link.getSource() instanceof ServiceOperationNode) {
						ServiceOperationNode nd = (ServiceOperationNode) link.getSource();
						tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode par = (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) nd.getParent();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(par.getName());
						ref.setOperationName(nd.getName());
						rL.getClients().getServiceOrModuleInstance().add(ref);
					}
					rL.setServer(factory.createRequestLinkServer());
					if (link.getTarget() instanceof ModuleOperationNode) {
						ModuleOperationNode nd = (ModuleOperationNode) link.getTarget();
						OpRefServer ref = factory.createOpRefServer();
						ref.setInstanceName(link.gettInst());
						ref.setOperationName(nd.getName());
						rL.getServer().setModuleInstance(ref);
					} else if (link.getTarget() instanceof ServiceOperationNode) {
						ServiceOperationNode nd = (ServiceOperationNode) link.getTarget();
						tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode par = (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) nd.getParent();
						OpRef ref = factory.createOpRef();
						ref.setInstanceName(par.getName());
						ref.setOperationName(nd.getName());
						rL.getServer().setReference(ref);
					}
					cImp.getDataLinkOrEventLinkOrRequestLink().add(rL);
					break;
				}
			}
			return cImp;
		} catch (Exception e) {
			EclipseUtil.writeStactTraceToConsole(e);
			return null;
		}
	}

	private static boolean contains(String name, Properties properties) {
		boolean ret = false;
		for (Parameter param : properties.getProperty()) {
			if (param.getName().equalsIgnoreCase(name)) {
				ret = true;
				break;
			}
		}
		return ret;
	}

	private static void populateOpParam(Object op, ModuleOperationNode pNode, ModuleOperationTypes type, ArrayList<Use> use) {
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node gNd : pNode.getChild()) {
			if (gNd instanceof ModuleOperationParameterNode) {
				ModuleOperationParameterNode gNode = (ModuleOperationParameterNode) gNd;
				String dType = gNode.getdType();
				String oType = gNode.getType();
				if (StringUtils.contains(dType, ":")) {
					String[] lib = StringUtils.split(dType, ":");
					Use add = new Use();
					add.setLibrary(lib[0]);
					if (!contains(use, add))
						use.add(add);
				}
				switch (type) {
				case DATA_READ:
					break;
				case DATA_WRITE:
					break;
				case EVENT_RECEIVED:
					EventReceived erVal = (EventReceived) op;
					switch (Enums.getParameterType(oType)) {
					case INPUT:
						Parameter param = new Parameter();
						param.setName(gNode.getName());
						param.setType(dType);
						erVal.getInput().add(param);
						break;
					case OUTPUT:
						break;
					}
					break;
				case EVENT_SENT:
					Event esVal = (Event) op;
					switch (Enums.getParameterType(oType)) {
					case INPUT:
						Parameter param = new Parameter();
						param.setName(gNode.getName());
						param.setType(dType);
						esVal.getInput().add(param);
						break;
					case OUTPUT:
						break;
					}
					break;
				case REQUEST_SENT:
					RequestSent rsVal = (RequestSent) op;
					switch (Enums.getParameterType(oType)) {
					case INPUT:
						Parameter iParam = new Parameter();
						iParam.setName(gNode.getName());
						iParam.setType(dType);
						rsVal.getInput().add(iParam);
						break;
					case OUTPUT:
						Parameter oParam = new Parameter();
						oParam.setName(gNode.getName());
						oParam.setType(dType);
						rsVal.getOutput().add(oParam);
						break;
					}
					break;
				case REQUEST_RECEIVED:
					RequestReceived rrVal = (RequestReceived) op;
					switch (Enums.getParameterType(oType)) {
					case INPUT:
						Parameter iParam = new Parameter();
						iParam.setName(gNode.getName());
						iParam.setType(dType);
						rrVal.getInput().add(iParam);
						break;
					case OUTPUT:
						Parameter oParam = new Parameter();
						oParam.setName(gNode.getName());
						oParam.setType(dType);
						rrVal.getOutput().add(oParam);
						break;
					}
					break;
				}
			}
		}
	}

	private static boolean contains(ArrayList<Use> use, Use add) {
		for (Use val : use) {
			if (val.getLibrary().equalsIgnoreCase(add.getLibrary()))
				return true;
		}
		return false;
	}
}

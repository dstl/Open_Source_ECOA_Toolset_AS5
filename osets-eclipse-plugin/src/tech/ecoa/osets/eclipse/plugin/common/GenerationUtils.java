/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.common;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.bind.JAXBException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.draw2d.geometry.Rectangle;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Enums;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ComputingNodeConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeploymentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingPlatformNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;
import tech.ecoa.osets.eclipse.plugin.util.CompDefUtil;
import tech.ecoa.osets.eclipse.plugin.util.ParseUtil;
import tech.ecoa.osets.eclipse.plugin.util.PluginUtil;
import tech.ecoa.osets.eclipse.plugin.util.ServicesUtil;
import tech.ecoa.osets.model.cdef.ComponentType;
import tech.ecoa.osets.model.cdef.ComponentTypeReference;
import tech.ecoa.osets.model.cdef.ComponentType.Service;
import tech.ecoa.osets.model.intf.Data;
import tech.ecoa.osets.model.intf.Event;
import tech.ecoa.osets.model.intf.Operation;
import tech.ecoa.osets.model.intf.RequestResponse;
import tech.ecoa.osets.model.intf.ServiceDefinition;

@SuppressWarnings("deprecation")
public class GenerationUtils {
	private static IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();

	public static void clearCreatedFolders(File root) {
		File[] files = root.listFiles();
		try {
			for (File file : files) {
				if (file.getName().equalsIgnoreCase("output")) {
					FileUtils.deleteDirectory(file);
				}
				if (file.getName().equalsIgnoreCase("4-ComponentImplementations")) {
					File[] child = file.listFiles();
					for (File c : child) {
						if (c.isFile())
							FileUtils.forceDelete(c);
						else {
							File[] gChild = c.listFiles();
							for (File gC : gChild) {
								if (gC.isDirectory())
									FileUtils.deleteDirectory(gC);
								else if (!StringUtils.endsWith(gC.getName(), ".impl.xml"))
									FileUtils.forceDelete(gC);
							}
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	public static boolean validate(String container) {
		if (container != null) {
			String[] names = StringUtils.split(container, "/");
			IResource resource = wsRoot.findMember(new Path("/" + names[0]));
			IPath loc = resource.getLocation();
			File prjLoc = new File(loc.toString());
			File[] res = prjLoc.listFiles();
			HashMap<String, ArrayList<File>> fileGroups = new HashMap<String, ArrayList<File>>();
			HashMap<String, Integer> groupCnt = new HashMap<String, Integer>();
			for (File file : res) {
				String extension = FilenameUtils.getExtension(file.getName());
				ArrayList<File> list = fileGroups.get(extension);
				int cnt = (groupCnt.get(extension) == null) ? 0 : groupCnt.get(extension).intValue();
				if (list == null)
					list = new ArrayList<File>();
				list.add(file);
				cnt++;
				groupCnt.put(extension, new Integer(cnt));
				fileGroups.put(extension, list);
			}
			return !validate(groupCnt);
		}
		return false;
	}

	private static boolean validate(HashMap<String, Integer> groupCnt) {
		boolean ret = false;
		Iterator<String> iter = groupCnt.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			Integer val = groupCnt.get(key);
			switch (key) {
			case "types":
				ret = ret || (val == 0);
				break;
			case "srvc":
				ret = ret || (val == 0);
				break;
			case "cdef":
				ret = ret || (val == 0);
				break;
			case "assmbl":
				ret = ret || (val == 0);
				break;
			case "cimpl":
				ret = ret || (val == 0);
				break;
			case "lsys":
				ret = ret || (val == 0);
				break;
			case "fassmbl":
				ret = ret || (val == 0);
				break;
			case "deploy":
				ret = ret || (val == 0);
				break;
			}
		}
		return ret;
	}

	public static String getFinalAssemblyfromInitialAssembly(String name, String containerName, String fileName) throws IOException {
		ArrayList<String> iAssmbl = new PluginUtil().getResourcesWithExtension("assmbl", containerName);
		String fName = "";
		for (String nm : iAssmbl) {
			if (StringUtils.contains(nm, name))
				fName = nm;
		}
		String content = FileUtils.readFileToString(new File(fName));
		CompositeNode comp = ParseUtil.getFinalAssemblyNodeFromText(content);
		String nm = FilenameUtils.getBaseName(fileName);
		if (comp.getDef() == null || comp.getDef().trim() == "")
			comp.setDef(comp.getName());
		comp.setName(nm);
		comp.setId(nm);
		content = ParseUtil.getIntFinalAssemblyEditorContent(comp);
		return content;
	}

	public static String getCompImplFromDef(String name, String containerName) throws JAXBException, IOException {
		String contents = "cimp^" + UUID.randomUUID().toString() + Constants.FLD_SEP + name + Constants.ROW_SEP;
		ComponentType cType = CompDefUtil.getInstance(containerName).getByName(name);
		for (Object obj : cType.getServiceOrReferenceOrProperty()) {
			if (obj instanceof Service) {
				Service srvc = (Service) obj;
				String pRef = UUID.randomUUID().toString();
				contents = contents + "service^" + pRef + Constants.FLD_SEP + srvc.getName() + Constants.FLD_SEP + srvc.getInterface().getValue().getSyntax() + Constants.FLD_SEP + Enums.ServiceTypes.PROVIDED + Constants.ROW_SEP;
				ServiceDefinition sDef = ServicesUtil.getInstance(containerName).getByName(srvc.getInterface().getValue().getSyntax());
				for (Operation ops : sDef.getOperations().getDataOrEventOrRequestresponse()) {
					String type = "";
					String dir = " ";
					if (ops instanceof Data) {
						type = Enums.ServiceOperationTypes.DATA.name();
					} else if (ops instanceof Event) {
						Event event = (Event) ops;
						type = Enums.ServiceOperationTypes.EVENT.name();
						dir = event.getDirection().name();
					} else if (ops instanceof RequestResponse) {
						type = Enums.ServiceOperationTypes.REQ_RES.name();
					}
					contents = contents + "sop^" + UUID.randomUUID().toString() + Constants.FLD_SEP + pRef + Constants.FLD_SEP + ops.getName() + Constants.FLD_SEP + type + Constants.FLD_SEP + "60,60,50,50" + Constants.FLD_SEP + dir + Constants.ROW_SEP;
				}
			} else if (obj instanceof ComponentTypeReference) {
				ComponentTypeReference ref = (ComponentTypeReference) obj;
				String pRef = UUID.randomUUID().toString();
				contents = contents + "service^" + pRef + Constants.FLD_SEP + ref.getName() + Constants.FLD_SEP + ref.getInterface().getValue().getSyntax() + Constants.FLD_SEP + Enums.ServiceTypes.REQUIRED + Constants.ROW_SEP;
				ServiceDefinition sDef = ServicesUtil.getInstance(containerName).getByName(ref.getInterface().getValue().getSyntax());
				for (Operation ops : sDef.getOperations().getDataOrEventOrRequestresponse()) {
					String type = "";
					String dir = " ";
					if (ops instanceof Data) {
						type = Enums.ServiceOperationTypes.DATA.name();
					} else if (ops instanceof Event) {
						Event event = (Event) ops;
						type = Enums.ServiceOperationTypes.EVENT.name();
						dir = event.getDirection().name();
					} else if (ops instanceof RequestResponse) {
						type = Enums.ServiceOperationTypes.REQ_RES.name();
					}
					contents = contents + "sop^" + UUID.randomUUID().toString() + Constants.FLD_SEP + pRef + Constants.FLD_SEP + ops.getName() + Constants.FLD_SEP + type + Constants.FLD_SEP + "60,60,50,50" + Constants.FLD_SEP + dir + Constants.ROW_SEP;
				}
			}
		}
		return contents;
	}

	public static String getDeploymentFromLSysAndFAssmbl(String name, String assmbl, String containerName) {
		PluginUtil util = new PluginUtil();
		LogicalSystemNode sys = util.getLogicalSystemDefinition(name, containerName);
		CompositeNode comp = util.getFinalAssemblyDefinition(assmbl, containerName);
		DeploymentNode node = new DeploymentNode();
		node.setfAssmbl(assmbl);
		node.setlSys(name);
		int i = 1;
		for (Node child : comp.getChild()) {
			if (child instanceof ComponentNode) {
				ComponentNode cNode = (ComponentNode) child;
				ComponentImplementationNode ciNode = util.getComponentImplementationDefinition(cNode.getInst(), containerName);
				ProtectionDomainNode pdNode = new ProtectionDomainNode();
				pdNode.setId(UUID.randomUUID().toString());
				pdNode.setName("PD_" + i);
				pdNode.setConstraints(new Rectangle(100, 100, 100, 100));
				pdNode.setParent(node);
				node.getChild().add(pdNode);
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node gChild : ciNode.getChild()) {
					if (gChild instanceof TriggerInstanceNode) {
						TriggerInstanceNode tiNode = (TriggerInstanceNode) gChild;
						DeployedTriggerInstanceNode dtiNode = new DeployedTriggerInstanceNode();
						dtiNode.setId(UUID.randomUUID().toString());
						dtiNode.setTriggerName(tiNode.getName());
						dtiNode.setPriority(tiNode.getPriority());
						dtiNode.setCompName(cNode.getName());
						dtiNode.setConstraints(new Rectangle(50, 50, 50, 50));
						dtiNode.setParent(pdNode);
						pdNode.getChild().add(dtiNode);
					}
					if (gChild instanceof DynamicTriggerInstanceNode) {
						DynamicTriggerInstanceNode tiNode = (DynamicTriggerInstanceNode) gChild;
						DeployedTriggerInstanceNode dtiNode = new DeployedTriggerInstanceNode();
						dtiNode.setId(UUID.randomUUID().toString());
						dtiNode.setTriggerName(tiNode.getName());
						dtiNode.setPriority(tiNode.getPriority());
						dtiNode.setCompName(cNode.getName());
						dtiNode.setConstraints(new Rectangle(50, 50, 50, 50));
						dtiNode.setParent(pdNode);
						pdNode.getChild().add(dtiNode);
					}
					if (gChild instanceof ModuleTypeNode) {
						for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node ggChild : gChild.getChild()) {
							if (ggChild instanceof ModuleInstanceNode) {
								ModuleInstanceNode miNode = (ModuleInstanceNode) ggChild;
								DeployedModuleInstanceNode dmiNode = new DeployedModuleInstanceNode();
								dmiNode.setId(UUID.randomUUID().toString());
								dmiNode.setCompName(cNode.getName());
								dmiNode.setModuleName(miNode.getName());
								dmiNode.setPriority(miNode.getPriority());
								dmiNode.setConstraints(new Rectangle(50, 50, 50, 50));
								dmiNode.setParent(pdNode);
								pdNode.getChild().add(dmiNode);
							}
						}
					}
				}
				i++;
			}
		}
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node child : sys.getChild()) {
			if (child instanceof LogicalComputingPlatformNode) {
				LogicalComputingPlatformNode lpNode = (LogicalComputingPlatformNode) child;
				PlatformConfigurationNode pcNode = new PlatformConfigurationNode();
				pcNode.setId(UUID.randomUUID().toString());
				pcNode.setCompPlatform(lpNode.getName());
				pcNode.setConstraints(new Rectangle(100, 100, 100, 100));
				pcNode.setParent(node);
				node.getChild().add(pcNode);
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node gChild : lpNode.getChild()) {
					if (gChild instanceof LogicalComputingNode) {
						LogicalComputingNode lcNode = (LogicalComputingNode) gChild;
						ComputingNodeConfigurationNode cncNode = new ComputingNodeConfigurationNode();
						cncNode.setId(UUID.randomUUID().toString());
						cncNode.setName(lcNode.getName());
						cncNode.setConstraints(new Rectangle(50, 50, 50, 50));
						cncNode.setParent(pcNode);
						pcNode.getChild().add(cncNode);
					}
				}
			}
		}
		String contents = ParseUtil.getIntDeploymentEditorContent(node);
		return contents;
	}
}

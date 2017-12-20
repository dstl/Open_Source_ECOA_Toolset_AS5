/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.util;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;

import com.sun.xml.internal.bind.v2.runtime.MarshallerImpl;

import tech.ecoa.osets.eclipse.plugin.common.Constants;
import tech.ecoa.osets.eclipse.plugin.common.NamespacePrefixMapper;
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

@SuppressWarnings("rawtypes")
public class ParseUtil {
	public static String removeEmptyTags(String string, Class clazz) {
		String ret = "";
		ArrayList<String> fin = new ArrayList<String>();
		if (StringUtils.contains(string, "\"\"")) {
			String[] brk = StringUtils.split(string, " ");
			for (String str : brk) {
				if (!StringUtils.contains(str, "\"\""))
					fin.add(str);
				else if (StringUtils.contains(str, "/>"))
					fin.add("/>");
				else if (StringUtils.contains(str, ">"))
					fin.add(">");
			}
		}
		if (fin.size() > 0)
			for (String val : fin) {
				ret += val + " ";
			}
		else
			ret = string;
		try {
			JAXBContext ctx = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = ctx.createUnmarshaller();
			MarshallerImpl marshaller = (MarshallerImpl) ctx.createMarshaller();
			marshaller.setProperty(MarshallerImpl.JAXB_FORMATTED_OUTPUT, true);
			marshaller.setProperty("com.sun.xml.internal.bind.namespacePrefixMapper", new NamespacePrefixMapper());
			StringWriter writer = new StringWriter();
			marshaller.marshal(unmarshaller.unmarshal(new StringReader(ret)), writer);
			ret = writer.toString();
		} catch (Exception e) {
		}
		return ret;
	}

	public static CompositeNode getInitialAssemblyNodeFromText(String text) {
		CompositeNode node = new CompositeNode();
		String[] recs = StringUtils.split(text, Constants.ROW_SEP);
		ArrayList<ComponentNode> compList = new ArrayList<ComponentNode>();
		ArrayList<CompositePropertyNode> cPropList = new ArrayList<CompositePropertyNode>();
		HashMap<String, ArrayList<ServiceNode>> sList = new HashMap<String, ArrayList<ServiceNode>>();
		HashMap<String, ArrayList<ReferenceNode>> rList = new HashMap<String, ArrayList<ReferenceNode>>();
		HashMap<String, ArrayList<ComponentPropertyNode>> cpPropList = new HashMap<String, ArrayList<ComponentPropertyNode>>();
		ArrayList<Link> links = new ArrayList<Link>();
		for (String val : recs) {
			String[] hdrs = StringUtils.split(val, Constants.DEF_SEP);
			switch (hdrs[0]) {
			case "composite":
				String[] roDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				node.setId(roDats[0]);
				node.setName((roDats.length > 1) ? roDats[1] : "");
				break;
			case "component":
				String[] cDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ComponentNode cNode = new ComponentNode();
				cNode.setParent(node);
				cNode.setId(cDats[0]);
				cNode.setName((cDats.length > 2 ? cDats[2] : ""));
				cNode.setType((cDats.length > 3) ? cDats[3] : "");
				String[] cLoc = StringUtils.split(cDats[1], Constants.VAL_SEP);
				cNode.setConstraints(new Rectangle(Integer.parseInt(cLoc[0]), Integer.parseInt(cLoc[1]), Integer.parseInt(cLoc[3]), Integer.parseInt(cLoc[2])));
				compList.add(cNode);
				break;
			case "composite-prop":
				String[] pDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				CompositePropertyNode pNode = new CompositePropertyNode();
				pNode.setParent(node);
				pNode.setId(pDats[0]);
				pNode.setName((pDats.length > 2) ? pDats[2] : "");
				pNode.setValue((pDats.length > 3) ? pDats[3] : "");
				pNode.setType((pDats.length > 4) ? pDats[4] : "");
				String[] pLoc = StringUtils.split(pDats[1], Constants.VAL_SEP);
				pNode.setConstraints(new Rectangle(Integer.parseInt(pLoc[0]), Integer.parseInt(pLoc[1]), Integer.parseInt(pLoc[3]), Integer.parseInt(pLoc[2])));
				cPropList.add(pNode);
				break;
			case "component-prop":
				String[] cpDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ComponentPropertyNode cpNode = new ComponentPropertyNode();
				cpNode.setId(cpDats[0]);
				cpNode.setName((cpDats.length > 3) ? cpDats[3] : "");
				cpNode.setValue((cpDats.length > 4) ? cpDats[4] : "");
				cpNode.setType((cpDats.length > 5) ? cpDats[5] : "");
				String[] cpLoc = StringUtils.split(cpDats[2], Constants.VAL_SEP);
				cpNode.setConstraints(new Rectangle(Integer.parseInt(cpLoc[0]), Integer.parseInt(cpLoc[1]), Integer.parseInt(cpLoc[3]), Integer.parseInt(cpLoc[2])));
				ArrayList<ComponentPropertyNode> cpNodes = cpPropList.get(cpDats[1]);
				if (cpNodes == null)
					cpNodes = new ArrayList<ComponentPropertyNode>();
				cpNodes.add(cpNode);
				cpPropList.put(cpDats[1], cpNodes);
				break;
			case "service":
				String[] sDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ServiceNode sNode = new ServiceNode();
				sNode.setId(sDats[0]);
				sNode.setName((sDats.length > 3) ? sDats[3] : "");
				sNode.setIntf((sDats.length > 4) ? sDats[4] : "");
				String[] sLoc = StringUtils.split(sDats[2], Constants.VAL_SEP);
				sNode.setConstraints(new Rectangle(Integer.parseInt(sLoc[0]), Integer.parseInt(sLoc[1]), Integer.parseInt(sLoc[3]), Integer.parseInt(sLoc[2])));
				ArrayList<ServiceNode> sNodes = sList.get(sDats[1]);
				if (sNodes == null)
					sNodes = new ArrayList<ServiceNode>();
				sNodes.add(sNode);
				sList.put(sDats[1], sNodes);
				break;
			case "reference":
				String[] rDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ReferenceNode rNode = new ReferenceNode();
				rNode.setId(rDats[0]);
				rNode.setName((rDats.length > 3 ? rDats[3] : ""));
				rNode.setIntf((rDats.length > 4) ? rDats[4] : "");
				String[] rLoc = StringUtils.split(rDats[2], Constants.VAL_SEP);
				rNode.setConstraints(new Rectangle(Integer.parseInt(rLoc[0]), Integer.parseInt(rLoc[1]), Integer.parseInt(rLoc[3]), Integer.parseInt(rLoc[2])));
				ArrayList<ReferenceNode> rNodes = rList.get(rDats[1]);
				if (rNodes == null)
					rNodes = new ArrayList<ReferenceNode>();
				rNodes.add(rNode);
				rList.put(rDats[1], rNodes);
				break;
			case "link":
				String[] lDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				String[] epDats = StringUtils.split(lDats[0], Constants.LNK_SEP);
				Link link = new Link();
				ServiceNode start = getLinkStart(sList, epDats[0]);
				ReferenceNode end = getLinkEnd(rList, epDats[1]);
				link.setRank((lDats.length > 1) ? lDats[1] : "");
				link.setName(((lDats.length > 2) ? lDats[2] : UUID.randomUUID().toString()));
				if (lDats.length > 3) {
					String[] bpDats = StringUtils.split(lDats[3], Constants.BP_SEP);
					for (String bpDat : bpDats) {
						String[] poDats = StringUtils.split(bpDat, Constants.VAL_SEP);
						Point p = new Point(Integer.parseInt(poDats[0]), Integer.parseInt(poDats[1]));
						link.getbPoints().add(p);
					}
				}
				link.setSource(start);
				link.setTarget(end);
				link.setId(start.getId() + ":" + end.getId());
				links.add(link);
				break;
			}
		}
		for (ComponentNode cNode : compList) {
			if (sList.containsKey(cNode.getId())) {
				ArrayList<ServiceNode> sNodes = sList.get(cNode.getId());
				for (ServiceNode sNode : sNodes) {
					Rectangle rect = sNode.getConstraints();
					sNode.setParent(cNode);
					Point p = sNode.getAnchor(new Point(rect.x, rect.y), 1);
					sNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					cNode.getChild().add(sNode);
				}
			}
			if (rList.containsKey(cNode.getId())) {
				ArrayList<ReferenceNode> rNodes = rList.get(cNode.getId());
				for (ReferenceNode rNode : rNodes) {
					Rectangle rect = rNode.getConstraints();
					rNode.setParent(cNode);
					Point p = rNode.getAnchor(new Point(rect.x, rect.y), 1);
					rNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					cNode.getChild().add(rNode);
				}
			}
			if (cpPropList.containsKey(cNode.getId())) {
				ArrayList<ComponentPropertyNode> cpNodes = cpPropList.get(cNode.getId());
				for (ComponentPropertyNode cpNode : cpNodes) {
					Rectangle rect = cpNode.getConstraints();
					cpNode.setParent(cNode);
					Point p = cpNode.getAnchor(new Point(rect.x, rect.y), 1);
					cpNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					cNode.getChild().add(cpNode);
				}
			}
		}
		for (Link link : links) {
			for (ComponentNode cNd : compList) {
				for (Node kNd : cNd.getChild()) {
					if (kNd instanceof ServiceNode) {
						ServiceNode sNode = (ServiceNode) kNd;
						if (isSource(link, sNode))
							sNode.getOutLinks().add(link);
					}
					if (kNd instanceof ReferenceNode) {
						ReferenceNode rNode = (ReferenceNode) kNd;
						if (isTarget(link, rNode))
							rNode.getInLinks().add(link);
					}
				}
			}
		}
		node.getChild().clear();
		node.getChild().addAll(compList);
		node.getChild().addAll(cPropList);
		node.getLinks().clear();
		node.getLinks().addAll(links);
		return node;
	}

	private static boolean isTarget(Link link, ReferenceNode rNode) {
		if (link.getTarget().equals(rNode))
			return true;
		else
			return false;
	}

	private static boolean isSource(Link link, ServiceNode sNode) {
		if (link.getSource().equals(sNode))
			return true;
		else
			return false;
	}

	private static ReferenceNode getLinkEnd(HashMap<String, ArrayList<ReferenceNode>> rList, String string) {
		Iterator<String> keys = rList.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			for (ReferenceNode rNode : rList.get(key)) {
				if (rNode.getId().equalsIgnoreCase(string))
					return rNode;
			}
		}
		return null;
	}

	private static ServiceNode getLinkStart(HashMap<String, ArrayList<ServiceNode>> sList, String string) {
		Iterator<String> keys = sList.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			for (ServiceNode sNode : sList.get(key)) {
				if (sNode.getId().equalsIgnoreCase(string))
					return sNode;
			}
		}
		return null;
	}

	public static tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode getFinalAssemblyNodeFromText(String text) {
		tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode node = new tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode();
		String[] recs = StringUtils.split(text, Constants.ROW_SEP);
		ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode> compList = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode>();
		ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode> cPropList = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode>();
		HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode>> sList = new HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode>>();
		HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode>> rList = new HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode>>();
		HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode>> cpPropList = new HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode>>();
		ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link> links = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link>();
		for (String val : recs) {
			String[] hdrs = StringUtils.split(val, Constants.DEF_SEP);
			switch (hdrs[0]) {
			case "composite":
				String[] roDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				node.setId(roDats[0]);
				node.setName((roDats.length > 1) ? roDats[1] : "");
				node.setDef((roDats.length > 2) ? roDats[2] : "");
				break;
			case "component":
				String[] cDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode cNode = new tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode();
				cNode.setParent(node);
				cNode.setId(cDats[0]);
				cNode.setName((cDats.length > 2 ? cDats[2] : ""));
				cNode.setType((cDats.length > 3) ? cDats[3] : "");
				cNode.setInst((cDats.length > 4) ? cDats[4] : "");
				String[] cLoc = StringUtils.split(cDats[1], Constants.VAL_SEP);
				cNode.setConstraints(new Rectangle(Integer.parseInt(cLoc[0]), Integer.parseInt(cLoc[1]), Integer.parseInt(cLoc[3]), Integer.parseInt(cLoc[2])));
				compList.add(cNode);
				break;
			case "composite-prop":
				String[] pDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode pNode = new tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode();
				pNode.setParent(node);
				pNode.setId(pDats[0]);
				pNode.setName((pDats.length > 2) ? pDats[2] : "");
				pNode.setValue((pDats.length > 3) ? pDats[3] : "");
				pNode.setType((pDats.length > 4) ? pDats[4] : "");
				String[] pLoc = StringUtils.split(pDats[1], Constants.VAL_SEP);
				pNode.setConstraints(new Rectangle(Integer.parseInt(pLoc[0]), Integer.parseInt(pLoc[1]), Integer.parseInt(pLoc[3]), Integer.parseInt(pLoc[2])));
				cPropList.add(pNode);
				break;
			case "component-prop":
				String[] cpDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode cpNode = new tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode();
				cpNode.setId(cpDats[0]);
				cpNode.setName((cpDats.length > 3) ? cpDats[3] : "");
				cpNode.setValue((cpDats.length > 4) ? cpDats[4] : "");
				cpNode.setType((cpDats.length > 5) ? cpDats[5] : "");
				String[] cpLoc = StringUtils.split(cpDats[2], Constants.VAL_SEP);
				cpNode.setConstraints(new Rectangle(Integer.parseInt(cpLoc[0]), Integer.parseInt(cpLoc[1]), Integer.parseInt(cpLoc[3]), Integer.parseInt(cpLoc[2])));
				ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode> cpNodes = cpPropList.get(cpDats[1]);
				if (cpNodes == null)
					cpNodes = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode>();
				cpNodes.add(cpNode);
				cpPropList.put(cpDats[1], cpNodes);
				break;
			case "service":
				String[] sDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode sNode = new tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode();
				sNode.setId(sDats[0]);
				sNode.setName((sDats.length > 3) ? sDats[3] : "");
				sNode.setIntf((sDats.length > 4) ? sDats[4] : "");
				String[] sLoc = StringUtils.split(sDats[2], Constants.VAL_SEP);
				sNode.setConstraints(new Rectangle(Integer.parseInt(sLoc[0]), Integer.parseInt(sLoc[1]), Integer.parseInt(sLoc[3]), Integer.parseInt(sLoc[2])));
				ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode> sNodes = sList.get(sDats[1]);
				if (sNodes == null)
					sNodes = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode>();
				sNodes.add(sNode);
				sList.put(sDats[1], sNodes);
				break;
			case "reference":
				String[] rDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode rNode = new tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode();
				rNode.setId(rDats[0]);
				rNode.setName((rDats.length > 3 ? rDats[3] : ""));
				rNode.setIntf((rDats.length > 4) ? rDats[4] : "");
				String[] rLoc = StringUtils.split(rDats[2], Constants.VAL_SEP);
				rNode.setConstraints(new Rectangle(Integer.parseInt(rLoc[0]), Integer.parseInt(rLoc[1]), Integer.parseInt(rLoc[3]), Integer.parseInt(rLoc[2])));
				ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode> rNodes = rList.get(rDats[1]);
				if (rNodes == null)
					rNodes = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode>();
				rNodes.add(rNode);
				rList.put(rDats[1], rNodes);
				break;
			case "link":
				String[] lDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				String[] epDats = StringUtils.split(lDats[0], Constants.LNK_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link link = new tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link();
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode start = getFAssmblLinkStart(sList, epDats[0]);
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode end = getFAssmblLinkEnd(rList, epDats[1]);
				link.setRank((lDats.length > 1) ? lDats[1] : "");
				link.setName(((lDats.length > 2) ? lDats[2] : UUID.randomUUID().toString()));
				if (lDats.length > 3) {
					String[] bpDats = StringUtils.split(lDats[3], Constants.BP_SEP);
					for (String bpDat : bpDats) {
						String[] poDats = StringUtils.split(bpDat, Constants.VAL_SEP);
						Point p = new Point(Integer.parseInt(poDats[0]), Integer.parseInt(poDats[1]));
						link.getbPoints().add(p);
					}
				}
				link.setName(((lDats.length > 3) ? lDats[3] : UUID.randomUUID().toString()));
				link.setSource(start);
				link.setTarget(end);
				link.setId(start.getId() + ":" + end.getId());
				links.add(link);
				break;
			}
		}
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode cNode : compList) {
			if (sList.containsKey(cNode.getId())) {
				ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode> sNodes = sList.get(cNode.getId());
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode sNode : sNodes) {
					Rectangle rect = sNode.getConstraints();
					sNode.setParent(cNode);
					Point p = sNode.getAnchor(new Point(rect.x, rect.y), 1);
					sNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					cNode.getChild().add(sNode);
				}
			}
			if (rList.containsKey(cNode.getId())) {
				ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode> rNodes = rList.get(cNode.getId());
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode rNode : rNodes) {
					Rectangle rect = rNode.getConstraints();
					rNode.setParent(cNode);
					Point p = rNode.getAnchor(new Point(rect.x, rect.y), 1);
					rNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					cNode.getChild().add(rNode);
				}
			}
			if (cpPropList.containsKey(cNode.getId())) {
				ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode> cpNodes = cpPropList.get(cNode.getId());
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode cpNode : cpNodes) {
					Rectangle rect = cpNode.getConstraints();
					cpNode.setParent(cNode);
					Point p = cpNode.getAnchor(new Point(rect.x, rect.y), 1);
					cpNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					cNode.getChild().add(cpNode);
				}
			}
		}
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link link : links) {
			for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode cNd : compList) {
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node kNd : cNd.getChild()) {
					if (kNd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode sNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode) kNd;
						if (isFAssmblSource(link, sNode))
							sNode.getOutLinks().add(link);
					}
					if (kNd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode rNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode) kNd;
						if (isFAssmblTarget(link, rNode))
							rNode.getInLinks().add(link);
					}
				}
			}
		}
		node.getChild().clear();
		node.getChild().addAll(compList);
		node.getChild().addAll(cPropList);
		node.getLinks().clear();
		node.getLinks().addAll(links);
		return node;
	}

	private static boolean isFAssmblTarget(tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link link, tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode rNode) {
		if (link.getTarget().equals(rNode))
			return true;
		else
			return false;
	}

	private static boolean isFAssmblSource(tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link link, tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode sNode) {
		if (link.getSource().equals(sNode))
			return true;
		else
			return false;
	}

	private static tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode getFAssmblLinkEnd(HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode>> rList, String string) {
		Iterator<String> keys = rList.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode rNode : rList.get(key)) {
				if (rNode.getId().equalsIgnoreCase(string))
					return rNode;
			}
		}
		return null;
	}

	private static tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode getFAssmblLinkStart(HashMap<String, ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode>> sList, String string) {
		Iterator<String> keys = sList.keySet().iterator();
		while (keys.hasNext()) {
			String key = keys.next();
			for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode sNode : sList.get(key)) {
				if (sNode.getId().equalsIgnoreCase(string))
					return sNode;
			}
		}
		return null;
	}

	public static LogicalSystemNode getLogicalSystemNodeFromText(String text) {
		LogicalSystemNode node = new LogicalSystemNode();
		String[] recs = StringUtils.split(text, Constants.ROW_SEP);
		ArrayList<LogicalComputingPlatformNode> cpNodes = new ArrayList<LogicalComputingPlatformNode>();
		HashMap<String, ArrayList<LogicalComputingNode>> ccNodes = new HashMap<String, ArrayList<LogicalComputingNode>>();
		HashMap<String, ArrayList<LogicalProcessorsNode>> lpNodes = new HashMap<String, ArrayList<LogicalProcessorsNode>>();
		for (String val : recs) {
			String[] hdrs = StringUtils.split(val, Constants.DEF_SEP);
			switch (hdrs[0]) {
			case "lsys":
				String[] roDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				node.setId(roDats[0]);
				node.setName((roDats.length > 1) ? roDats[1] : "");
				break;
			case "platform":
				String[] cDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				LogicalComputingPlatformNode cNode = new LogicalComputingPlatformNode();
				cNode.setParent(node);
				cNode.setId(cDats[0]);
				cNode.setName((cDats.length > 2 ? cDats[2] : ""));
				String[] cLoc = StringUtils.split(cDats[1], Constants.VAL_SEP);
				cNode.setConstraints(new Rectangle(Integer.parseInt(cLoc[0]), Integer.parseInt(cLoc[1]), Integer.parseInt(cLoc[3]), Integer.parseInt(cLoc[2])));
				cpNodes.add(cNode);
				break;
			case "node":
				String[] pDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				LogicalComputingNode pNode = new LogicalComputingNode();
				pNode.setParent(node);
				pNode.setId(pDats[0]);
				pNode.setName((pDats.length > 3) ? pDats[3] : "");
				pNode.setAvailMem((pDats.length > 4) ? pDats[4] : "");
				pNode.setEndianess((pDats.length > 5) ? pDats[5] : "");
				pNode.setMst((pDats.length > 6) ? pDats[6] : "");
				pNode.setOsName((pDats.length > 7) ? pDats[7] : "");
				pNode.setOsVer((pDats.length > 8) ? pDats[8] : "");
				String[] pLoc = StringUtils.split(pDats[2], Constants.VAL_SEP);
				pNode.setConstraints(new Rectangle(Integer.parseInt(pLoc[0]), Integer.parseInt(pLoc[1]), Integer.parseInt(pLoc[3]), Integer.parseInt(pLoc[2])));
				ArrayList<LogicalComputingNode> sNodes = ccNodes.get(pDats[1]);
				if (sNodes == null)
					sNodes = new ArrayList<LogicalComputingNode>();
				sNodes.add(pNode);
				ccNodes.put(pDats[1], sNodes);
				break;
			case "proc":
				String[] lpDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				LogicalProcessorsNode lpNode = new LogicalProcessorsNode();
				lpNode.setParent(node);
				lpNode.setId(lpDats[0]);
				lpNode.setNum((lpDats.length > 3) ? lpDats[3] : "");
				lpNode.setType((lpDats.length > 4) ? lpDats[4] : "");
				lpNode.setStepDur((lpDats.length > 5) ? lpDats[5] : "");
				String[] lpLoc = StringUtils.split(lpDats[2], Constants.VAL_SEP);
				lpNode.setConstraints(new Rectangle(Integer.parseInt(lpLoc[0]), Integer.parseInt(lpLoc[1]), Integer.parseInt(lpLoc[3]), Integer.parseInt(lpLoc[2])));
				ArrayList<LogicalProcessorsNode> lsNodes = lpNodes.get(lpDats[1]);
				if (lsNodes == null)
					lsNodes = new ArrayList<LogicalProcessorsNode>();
				lsNodes.add(lpNode);
				lpNodes.put(lpDats[1], lsNodes);
				break;
			}
		}
		for (LogicalComputingPlatformNode pNode : cpNodes) {
			if (ccNodes.containsKey(pNode.getId())) {
				ArrayList<LogicalComputingNode> cNodes = ccNodes.get(pNode.getId());
				for (LogicalComputingNode cNode : cNodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(pNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					pNode.getChild().add(cNode);
					if (lpNodes.containsKey(cNode.getId())) {
						ArrayList<LogicalProcessorsNode> gNodes = lpNodes.get(cNode.getId());
						for (LogicalProcessorsNode gNode : gNodes) {
							rect = gNode.getConstraints();
							gNode.setParent(cNode);
							p = gNode.getAnchor(new Point(rect.x, rect.y), 2);
							gNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
							cNode.getChild().add(gNode);
						}
					}
				}
			}
		}
		node.getChild().clear();
		node.getChild().addAll(cpNodes);
		return node;
	}

	public static DeploymentNode getDeploymentNodeFromText(String text) {
		DeploymentNode node = new DeploymentNode();
		String[] recs = StringUtils.split(text, Constants.ROW_SEP);
		ArrayList<ProtectionDomainNode> pdNodes = new ArrayList<ProtectionDomainNode>();
		ArrayList<PlatformConfigurationNode> pcNodes = new ArrayList<PlatformConfigurationNode>();
		HashMap<String, ArrayList<ComputingNodeConfigurationNode>> cncNodes = new HashMap<String, ArrayList<ComputingNodeConfigurationNode>>();
		HashMap<String, ArrayList<DeployedModuleInstanceNode>> dmNodes = new HashMap<String, ArrayList<DeployedModuleInstanceNode>>();
		HashMap<String, ArrayList<DeployedTriggerInstanceNode>> dtNodes = new HashMap<String, ArrayList<DeployedTriggerInstanceNode>>();
		for (String val : recs) {
			String[] hdrs = StringUtils.split(val, Constants.DEF_SEP);
			switch (hdrs[0]) {
			case "deploy":
				String[] roDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				node.setId(roDats[0]);
				node.setlSys((roDats.length > 1) ? roDats[1] : "");
				node.setfAssmbl((roDats.length > 2) ? roDats[2] : "");
				break;
			case "protdom":
				String[] pdDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ProtectionDomainNode pdNode = new ProtectionDomainNode();
				pdNode.setParent(node);
				pdNode.setId(pdDats[0]);
				pdNode.setName((pdDats.length > 2 ? pdDats[2] : ""));
				pdNode.setEoCompNode((pdDats.length > 3 ? pdDats[3] : ""));
				pdNode.setEoCompPlatform((pdDats.length > 4 ? pdDats[4] : ""));
				String[] pdLoc = StringUtils.split(pdDats[1], Constants.VAL_SEP);
				pdNode.setConstraints(new Rectangle(Integer.parseInt(pdLoc[0]), Integer.parseInt(pdLoc[1]), Integer.parseInt(pdLoc[3]), Integer.parseInt(pdLoc[2])));
				pdNodes.add(pdNode);
				break;
			case "platform":
				String[] pcDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				PlatformConfigurationNode pcNode = new PlatformConfigurationNode();
				pcNode.setParent(node);
				pcNode.setId(pcDats[0]);
				pcNode.setCompPlatform((pcDats.length > 2 ? pcDats[2] : ""));
				pcNode.setNotifMaxNumber((pcDats.length > 3 ? pcDats[3] : ""));
				pcNode.setMcastAddr((pcDats.length > 4 ? pcDats[4] : ""));
				pcNode.setPort((pcDats.length > 5 ? pcDats[5] : ""));
				pcNode.setPlatNum((pcDats.length > 6 ? pcDats[6] : ""));
				String[] pcLoc = StringUtils.split(pcDats[1], Constants.VAL_SEP);
				pcNode.setConstraints(new Rectangle(Integer.parseInt(pcLoc[0]), Integer.parseInt(pcLoc[1]), Integer.parseInt(pcLoc[3]), Integer.parseInt(pcLoc[2])));
				pcNodes.add(pcNode);
				break;
			case "dmod":
				String[] dDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				DeployedModuleInstanceNode dNode = new DeployedModuleInstanceNode();
				dNode.setParent(node);
				dNode.setId(dDats[0]);
				dNode.setCompName((dDats.length > 3) ? dDats[3] : "");
				dNode.setModuleName((dDats.length > 4) ? dDats[4] : "");
				dNode.setPriority((dDats.length > 5) ? dDats[5] : "");
				String[] dLoc = StringUtils.split(dDats[2], Constants.VAL_SEP);
				dNode.setConstraints(new Rectangle(Integer.parseInt(dLoc[0]), Integer.parseInt(dLoc[1]), Integer.parseInt(dLoc[3]), Integer.parseInt(dLoc[2])));
				ArrayList<DeployedModuleInstanceNode> sNodes = dmNodes.get(dDats[1]);
				if (sNodes == null)
					sNodes = new ArrayList<DeployedModuleInstanceNode>();
				sNodes.add(dNode);
				dmNodes.put(dDats[1], sNodes);
				break;
			case "dtrig":
				String[] tDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				DeployedTriggerInstanceNode tNode = new DeployedTriggerInstanceNode();
				tNode.setParent(node);
				tNode.setId(tDats[0]);
				tNode.setCompName((tDats.length > 3) ? tDats[3] : "");
				tNode.setTriggerName((tDats.length > 4) ? tDats[4] : "");
				tNode.setPriority((tDats.length > 5) ? tDats[5] : "");
				String[] tLoc = StringUtils.split(tDats[2], Constants.VAL_SEP);
				tNode.setConstraints(new Rectangle(Integer.parseInt(tLoc[0]), Integer.parseInt(tLoc[1]), Integer.parseInt(tLoc[3]), Integer.parseInt(tLoc[2])));
				ArrayList<DeployedTriggerInstanceNode> tNodes = dtNodes.get(tDats[1]);
				if (tNodes == null)
					tNodes = new ArrayList<DeployedTriggerInstanceNode>();
				tNodes.add(tNode);
				dtNodes.put(tDats[1], tNodes);
				break;
			case "cnc":
				String[] cDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ComputingNodeConfigurationNode cNode = new ComputingNodeConfigurationNode();
				cNode.setParent(node);
				cNode.setId(cDats[0]);
				cNode.setName((cDats.length > 3) ? cDats[3] : "");
				cNode.setSchedInfo((cDats.length > 4) ? cDats[4] : "");
				String[] cLoc = StringUtils.split(cDats[2], Constants.VAL_SEP);
				cNode.setConstraints(new Rectangle(Integer.parseInt(cLoc[0]), Integer.parseInt(cLoc[1]), Integer.parseInt(cLoc[3]), Integer.parseInt(cLoc[2])));
				ArrayList<ComputingNodeConfigurationNode> cNodes = cncNodes.get(cDats[1]);
				if (cNodes == null)
					cNodes = new ArrayList<ComputingNodeConfigurationNode>();
				cNodes.add(cNode);
				cncNodes.put(cDats[1], cNodes);
				break;
			}
		}
		for (ProtectionDomainNode pNode : pdNodes) {
			if (dmNodes.containsKey(pNode.getId())) {
				ArrayList<DeployedModuleInstanceNode> cNodes = dmNodes.get(pNode.getId());
				for (DeployedModuleInstanceNode cNode : cNodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(pNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					pNode.getChild().add(cNode);
				}
			}
			if (dtNodes.containsKey(pNode.getId())) {
				ArrayList<DeployedTriggerInstanceNode> cNodes = dtNodes.get(pNode.getId());
				for (DeployedTriggerInstanceNode cNode : cNodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(pNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					pNode.getChild().add(cNode);
				}
			}
		}
		for (PlatformConfigurationNode pNode : pcNodes) {
			if (cncNodes.containsKey(pNode.getId())) {
				ArrayList<ComputingNodeConfigurationNode> cNodes = cncNodes.get(pNode.getId());
				for (ComputingNodeConfigurationNode cNode : cNodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(pNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					pNode.getChild().add(cNode);
				}
			}
		}
		node.getChild().clear();
		node.getChild().addAll(pdNodes);
		node.getChild().addAll(pcNodes);
		return node;
	}

	public static ComponentImplementationNode getComponentImplementationNodeFromText(String text) {
		ComponentImplementationNode node = new ComponentImplementationNode();
		String[] recs = StringUtils.split(text, Constants.ROW_SEP);
		ArrayList<TriggerInstanceNode> tList = new ArrayList<TriggerInstanceNode>();
		ArrayList<DynamicTriggerInstanceNode> dtList = new ArrayList<DynamicTriggerInstanceNode>();
		ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode> sList = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode>();
		ArrayList<ModuleTypeNode> mtList = new ArrayList<ModuleTypeNode>();
		HashMap<String, ArrayList<ServiceOperationNode>> soList = new HashMap<String, ArrayList<ServiceOperationNode>>();
		HashMap<String, ArrayList<ModuleImplementationNode>> implList = new HashMap<String, ArrayList<ModuleImplementationNode>>();
		HashMap<String, ArrayList<ModuleInstanceNode>> instList = new HashMap<String, ArrayList<ModuleInstanceNode>>();
		HashMap<String, ArrayList<ModuleOperationNode>> moList = new HashMap<String, ArrayList<ModuleOperationNode>>();
		HashMap<String, ArrayList<ModuleOperationParameterNode>> mopList = new HashMap<String, ArrayList<ModuleOperationParameterNode>>();
		HashMap<String, ArrayList<ModuleInstancePropertyNode>> propList = new HashMap<String, ArrayList<ModuleInstancePropertyNode>>();
		HashMap<String, ArrayList<TriggerInstanceTerminalNode>> tiList = new HashMap<String, ArrayList<TriggerInstanceTerminalNode>>();
		HashMap<String, ArrayList<DynamicTriggerInstanceTerminalNode>> dtiList = new HashMap<String, ArrayList<DynamicTriggerInstanceTerminalNode>>();
		ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link> links = new ArrayList<tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link>();
		for (String val : recs) {
			String[] hdrs = StringUtils.split(val, Constants.DEF_SEP);
			switch (hdrs[0]) {
			case "cimp":
				String[] roDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				node.setId(roDats[0].trim());
				node.setName((roDats.length > 1) ? roDats[1].trim() : "");
				break;
			case "trig":
				String[] cDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				TriggerInstanceNode cNode = new TriggerInstanceNode();
				cNode.setParent(node);
				cNode.setId(cDats[0].trim());
				cNode.setName((cDats.length > 2 ? cDats[2].trim() : ""));
				cNode.setPriority((cDats.length > 3) ? cDats[3].trim() : "");
				String[] cLoc = StringUtils.split(cDats[1], Constants.VAL_SEP);
				cNode.setConstraints(new Rectangle(Integer.parseInt(cLoc[0]), Integer.parseInt(cLoc[1]), Integer.parseInt(cLoc[3]), Integer.parseInt(cLoc[2])));
				tList.add(cNode);
				break;
			case "titer":
				String[] titerDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				TriggerInstanceTerminalNode titerNode = new TriggerInstanceTerminalNode();
				titerNode.setId(titerDats[0]);
				String[] titerLoc = StringUtils.split(titerDats[2], Constants.VAL_SEP);
				titerNode.setConstraints(new Rectangle(Integer.parseInt(titerLoc[0].trim()), Integer.parseInt(titerLoc[1].trim()), Integer.parseInt(titerLoc[3].trim()), Integer.parseInt(titerLoc[2].trim())));
				ArrayList<TriggerInstanceTerminalNode> titerNodes = tiList.get(titerDats[1]);
				if (titerNodes == null)
					titerNodes = new ArrayList<TriggerInstanceTerminalNode>();
				titerNodes.add(titerNode);
				tiList.put(titerDats[1], titerNodes);
				break;
			case "dtrig":
				String[] pDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				DynamicTriggerInstanceNode pNode = new DynamicTriggerInstanceNode();
				pNode.setParent(node);
				pNode.setId(pDats[0]);
				pNode.setName((pDats.length > 2) ? pDats[2].trim() : "");
				pNode.setPriority((pDats.length > 3) ? pDats[3].trim() : "");
				String[] pLoc = StringUtils.split(pDats[1], Constants.VAL_SEP);
				pNode.setConstraints(new Rectangle(Integer.parseInt(pLoc[0]), Integer.parseInt(pLoc[1]), Integer.parseInt(pLoc[3]), Integer.parseInt(pLoc[2])));
				dtList.add(pNode);
				break;
			case "dtiter":
				String[] dtiterDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				DynamicTriggerInstanceTerminalNode dtiterNode = new DynamicTriggerInstanceTerminalNode();
				dtiterNode.setId(dtiterDats[0]);
				dtiterNode.setName((dtiterDats.length > 2) ? dtiterDats[3].trim() : "");
				dtiterNode.setType((dtiterDats.length > 3) ? dtiterDats[3].trim() : "");
				String[] dtiterLoc = StringUtils.split(dtiterDats[4], Constants.VAL_SEP);
				dtiterNode.setConstraints(new Rectangle(Integer.parseInt(dtiterLoc[0].trim()), Integer.parseInt(dtiterLoc[1].trim()), Integer.parseInt(dtiterLoc[3].trim()), Integer.parseInt(dtiterLoc[2].trim())));
				ArrayList<DynamicTriggerInstanceTerminalNode> dtiterNodes = dtiList.get(dtiterDats[1]);
				if (dtiterNodes == null)
					dtiterNodes = new ArrayList<DynamicTriggerInstanceTerminalNode>();
				dtiterNodes.add(dtiterNode);
				dtiList.put(dtiterDats[1], dtiterNodes);
				break;
			case "service":
				String[] cpDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode cpNode = new tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode();
				cpNode.setId(cpDats[0].trim());
				cpNode.setName((cpDats.length > 1) ? cpDats[1].trim() : "");
				cpNode.setIntf((cpDats.length > 2) ? cpDats[2].trim() : "");
				cpNode.setType((cpDats.length > 3) ? cpDats[3].trim() : "");
				String[] cpLoc = ((cpDats.length > 4) ? (StringUtils.split(cpDats[4], Constants.VAL_SEP)) : new String[] { "100", "100", "100", "100" });
				cpNode.setConstraints(new Rectangle(Integer.parseInt(cpLoc[0]), Integer.parseInt(cpLoc[1]), Integer.parseInt(cpLoc[3]), Integer.parseInt(cpLoc[2])));
				cpNode.setParent(node);
				sList.add(cpNode);
				break;
			case "sop":
				String[] sDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ServiceOperationNode sNode = new ServiceOperationNode();
				sNode.setId(sDats[0]);
				sNode.setName((sDats.length > 2) ? sDats[2].trim() : "");
				sNode.setType((sDats.length > 3) ? sDats[3].trim() : "");
				sNode.setDir((sDats.length > 5) ? sDats[5].trim() : "");
				String[] sLoc;
				if (sDats.length > 4) {
					String loc = sDats[4].trim();
					sLoc = StringUtils.split(loc, Constants.VAL_SEP);
					if (sLoc.length != 4)
						sLoc = new String[] { "60", "60", "50", "50" };
				} else {
					sLoc = new String[] { "60", "60", "50", "50" };
				}
				sNode.setConstraints(new Rectangle(Integer.parseInt(sLoc[0].trim()), Integer.parseInt(sLoc[1].trim()), Integer.parseInt(sLoc[3].trim()), Integer.parseInt(sLoc[2].trim())));
				ArrayList<ServiceOperationNode> sNodes = soList.get(sDats[1]);
				if (sNodes == null)
					sNodes = new ArrayList<ServiceOperationNode>();
				sNodes.add(sNode);
				soList.put(sDats[1], sNodes);
				break;
			case "mod":
				String[] rDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ModuleTypeNode rNode = new ModuleTypeNode();
				rNode.setId(rDats[0]);
				rNode.setName((rDats.length > 1 ? rDats[1].trim() : ""));
				rNode.setSup((rDats.length > 2) ? Boolean.valueOf(rDats[2].trim()) : false);
				String[] rLoc = ((rDats.length > 3) ? (StringUtils.split(rDats[3], Constants.VAL_SEP)) : new String[] { "60", "60", "50", "50" });
				rNode.setConstraints(new Rectangle(Integer.parseInt(rLoc[0].trim()), Integer.parseInt(rLoc[1].trim()), Integer.parseInt(rLoc[3].trim()), Integer.parseInt(rLoc[2].trim())));
				rNode.setParent(node);
				mtList.add(rNode);
				break;
			case "impl":
				String[] iDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ModuleImplementationNode iNode = new ModuleImplementationNode();
				iNode.setId(iDats[0]);
				iNode.setName((iDats.length > 3 ? iDats[3].trim() : ""));
				iNode.setType((iDats.length > 4 ? iDats[4].trim() : ""));
				iNode.setLang((iDats.length > 5 ? iDats[5].trim() : ""));
				String[] iLoc = StringUtils.split(iDats[2], Constants.VAL_SEP);
				iNode.setConstraints(new Rectangle(Integer.parseInt(iLoc[0].trim()), Integer.parseInt(iLoc[1].trim()), Integer.parseInt(iLoc[3].trim()), Integer.parseInt(iLoc[2].trim())));
				ArrayList<ModuleImplementationNode> rNodes = implList.get(iDats[1]);
				if (rNodes == null)
					rNodes = new ArrayList<ModuleImplementationNode>();
				rNodes.add(iNode);
				implList.put(iDats[1], rNodes);
				break;
			case "inst":
				String[] inDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ModuleInstanceNode inNode = new ModuleInstanceNode();
				inNode.setId(inDats[0]);
				inNode.setName((inDats.length > 3 ? inDats[3].trim() : ""));
				inNode.setImpl((inDats.length > 4 ? inDats[4].trim() : ""));
				inNode.setPriority((inDats.length > 5 ? inDats[5].trim() : ""));
				String[] inLoc = StringUtils.split(inDats[2], Constants.VAL_SEP);
				inNode.setConstraints(new Rectangle(Integer.parseInt(inLoc[0].trim()), Integer.parseInt(inLoc[1].trim()), Integer.parseInt(inLoc[3].trim()), Integer.parseInt(inLoc[2].trim())));
				ArrayList<ModuleInstanceNode> inNodes = instList.get(inDats[1]);
				if (inNodes == null)
					inNodes = new ArrayList<ModuleInstanceNode>();
				inNodes.add(inNode);
				instList.put(inDats[1], inNodes);
				break;
			case "mop":
				String[] mopDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ModuleOperationNode mopNode = new ModuleOperationNode();
				mopNode.setId(mopDats[0].trim());
				mopNode.setName((mopDats.length > 3 ? mopDats[3].trim() : ""));
				mopNode.setType((mopDats.length > 4) ? mopDats[4].trim() : "");
				mopNode.setSync((mopDats.length > 5) ? Boolean.valueOf(mopDats[5].trim()) : false);
				mopNode.setTimeout((mopDats.length > 6) ? mopDats[6].trim() : "");
				mopNode.setdType((mopDats.length > 7) ? mopDats[7].trim() : "");
				String[] mopLoc = StringUtils.split(mopDats[2], Constants.VAL_SEP);
				mopNode.setConstraints(new Rectangle(Integer.parseInt(mopLoc[0].trim()), Integer.parseInt(mopLoc[1].trim()), Integer.parseInt(mopLoc[3].trim()), Integer.parseInt(mopLoc[2].trim())));
				ArrayList<ModuleOperationNode> mopNodes = moList.get(mopDats[1]);
				if (mopNodes == null)
					mopNodes = new ArrayList<ModuleOperationNode>();
				mopNodes.add(mopNode);
				moList.put(mopDats[1], mopNodes);
				break;
			case "mopp":
				String[] moppDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ModuleOperationParameterNode moppNode = new ModuleOperationParameterNode();
				moppNode.setId(moppDats[0]);
				moppNode.setName((moppDats.length > 3 ? moppDats[3].trim() : ""));
				moppNode.setType((moppDats.length > 4) ? moppDats[4].trim() : "");
				moppNode.setdType((moppDats.length > 5) ? moppDats[5].trim() : "");
				String[] moppLoc = StringUtils.split(moppDats[2], Constants.VAL_SEP);
				moppNode.setConstraints(new Rectangle(Integer.parseInt(moppLoc[0].trim()), Integer.parseInt(moppLoc[1].trim()), Integer.parseInt(moppLoc[3].trim()), Integer.parseInt(moppLoc[2].trim())));
				ArrayList<ModuleOperationParameterNode> moppNodes = mopList.get(moppDats[1]);
				if (moppNodes == null)
					moppNodes = new ArrayList<ModuleOperationParameterNode>();
				moppNodes.add(moppNode);
				mopList.put(moppDats[1], moppNodes);
				break;
			case "prop":
				String[] propDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				ModuleInstancePropertyNode propNode = new ModuleInstancePropertyNode();
				propNode.setId(propDats[0]);
				propNode.setName((propDats.length > 3 ? propDats[3].trim() : ""));
				propNode.setType((propDats.length > 4) ? propDats[4].trim() : "");
				propNode.setValue((propDats.length > 5) ? propDats[5].trim() : "");
				String[] propLoc = StringUtils.split(propDats[2], Constants.VAL_SEP);
				propNode.setConstraints(new Rectangle(Integer.parseInt(propLoc[0].trim()), Integer.parseInt(propLoc[1].trim()), Integer.parseInt(propLoc[3].trim()), Integer.parseInt(propLoc[2].trim())));
				ArrayList<ModuleInstancePropertyNode> propNodes = propList.get(propDats[1]);
				if (propNodes == null)
					propNodes = new ArrayList<ModuleInstancePropertyNode>();
				propNodes.add(propNode);
				propList.put(propDats[1], propNodes);
				break;
			case "link":
				String[] lDats = StringUtils.split(hdrs[1], Constants.FLD_SEP);
				String[] epDats = StringUtils.split(lDats[0], Constants.LNK_SEP);
				tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link link = new tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link();
				tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node src = getNodeById(epDats[0], tiList, dtiList, soList, moList);
				tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node trg = getNodeById(epDats[1], tiList, dtiList, soList, moList);
				if (src != null && trg != null) {
					link.setSource(src);
					link.setTarget(trg);
					src.getOutLinks().add(link);
					trg.getInLinks().add(link);
					link.setId((lDats.length > 0) ? lDats[0].trim() : "");
					link.setType((lDats.length > 1) ? lDats[1].trim() : "");
					link.setsInst((lDats.length > 2) ? lDats[2].trim() : "");
					link.settInst((lDats.length > 3) ? lDats[3].trim() : "");
					link.setPeriod((lDats.length > 4) ? lDats[4].trim() : "");
					link.setName(((lDats.length > 5) ? lDats[5] : UUID.randomUUID().toString()));
					if (lDats.length > 6) {
						String[] bpDats = StringUtils.split(lDats[6], Constants.BP_SEP);
						for (String bpDat : bpDats) {
							String[] poDats = StringUtils.split(bpDat, Constants.VAL_SEP);
							Point p = new Point(Integer.parseInt(poDats[0]), Integer.parseInt(poDats[1]));
							link.getbPoints().add(p);
						}
					}
					link.setId(src.getId() + ":" + trg.getId());
					links.add(link);
				}
				break;
			}
		}
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode sNode : sList) {
			if (soList.containsKey(sNode.getId())) {
				ArrayList<ServiceOperationNode> nodes = soList.get(sNode.getId());
				for (ServiceOperationNode cNode : nodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(sNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					sNode.getChild().add(cNode);
				}
			}
		}
		for (ModuleTypeNode mtNode : mtList) {
			if (implList.containsKey(mtNode.getId())) {
				ArrayList<ModuleImplementationNode> nodes = implList.get(mtNode.getId());
				for (ModuleImplementationNode cNode : nodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(mtNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					mtNode.getChild().add(cNode);
				}
			}
			if (instList.containsKey(mtNode.getId())) {
				ArrayList<ModuleInstanceNode> nodes = instList.get(mtNode.getId());
				for (ModuleInstanceNode cNode : nodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(mtNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					if (propList.containsKey(cNode.getId())) {
						ArrayList<ModuleInstancePropertyNode> mipNodes = propList.get(cNode.getId());
						for (ModuleInstancePropertyNode mipNode : mipNodes) {
							rect = mipNode.getConstraints();
							mipNode.setParent(cNode);
							p = mipNode.getAnchor(new Point(rect.x, rect.y), 2);
							mipNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
							cNode.getChild().add(mipNode);
						}
					}
					mtNode.getChild().add(cNode);
				}
			}
			if (moList.containsKey(mtNode.getId())) {
				ArrayList<ModuleOperationNode> nodes = moList.get(mtNode.getId());
				for (ModuleOperationNode moNode : nodes) {
					Rectangle rect = moNode.getConstraints();
					moNode.setParent(mtNode);
					Point p = moNode.getAnchor(new Point(rect.x, rect.y), 1);
					moNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					if (mopList.containsKey(moNode.getId())) {
						ArrayList<ModuleOperationParameterNode> cNodes = mopList.get(moNode.getId());
						for (ModuleOperationParameterNode cNode : cNodes) {
							rect = cNode.getConstraints();
							cNode.setParent(moNode);
							p = cNode.getAnchor(new Point(rect.x, rect.y), 2);
							cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
							moNode.getChild().add(cNode);
						}
					}
					mtNode.getChild().add(moNode);
				}
			}
		}
		for (TriggerInstanceNode tiNode : tList) {
			if (tiList.containsKey(tiNode.getId())) {
				ArrayList<TriggerInstanceTerminalNode> nodes = tiList.get(tiNode.getId());
				for (TriggerInstanceTerminalNode cNode : nodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(tiNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					tiNode.getChild().add(cNode);
				}
			}
		}
		for (DynamicTriggerInstanceNode dtiNode : dtList) {
			if (dtiList.containsKey(dtiNode.getId())) {
				ArrayList<DynamicTriggerInstanceTerminalNode> nodes = dtiList.get(dtiNode.getId());
				for (DynamicTriggerInstanceTerminalNode cNode : nodes) {
					Rectangle rect = cNode.getConstraints();
					cNode.setParent(dtiNode);
					Point p = cNode.getAnchor(new Point(rect.x, rect.y), 1);
					cNode.setConstraints(new Rectangle(p.x, p.y, rect.width, rect.height));
					dtiNode.getChild().add(cNode);
				}
			}
		}
		node.getChild().clear();
		node.getChild().addAll(tList);
		node.getChild().addAll(dtList);
		node.getChild().addAll(sList);
		node.getChild().addAll(mtList);
		node.getLinks().clear();
		node.getLinks().addAll(links);
		return node;
	}

	private static tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node getNodeById(String string, HashMap<String, ArrayList<TriggerInstanceTerminalNode>> tiList, HashMap<String, ArrayList<DynamicTriggerInstanceTerminalNode>> dtiList, HashMap<String, ArrayList<ServiceOperationNode>> soList, HashMap<String, ArrayList<ModuleOperationNode>> moList) {
		Iterator<String> iter = tiList.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			for (TriggerInstanceTerminalNode nd : tiList.get(key)) {
				if (nd.getId().equalsIgnoreCase(string))
					return nd;
			}
		}
		iter = dtiList.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			for (DynamicTriggerInstanceTerminalNode nd : dtiList.get(key)) {
				if (nd.getId().equalsIgnoreCase(string))
					return nd;
			}
		}
		iter = soList.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			for (ServiceOperationNode nd : soList.get(key)) {
				if (nd.getId().equalsIgnoreCase(string))
					return nd;
			}
		}
		iter = moList.keySet().iterator();
		while (iter.hasNext()) {
			String key = iter.next();
			for (ModuleOperationNode nd : moList.get(key)) {
				if (nd.getId().equalsIgnoreCase(string))
					return nd;
			}
		}
		return null;
	}

	public static String getLogicalSysEditorContent(LogicalSystemNode node) {
		String content = "";
		content = content + "lsys" + Constants.DEF_SEP + ((node.getId() != null) ? node.getId() : " ") + Constants.FLD_SEP + ((node.getName() != null) ? node.getName() : " ") + Constants.ROW_SEP;
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node nd : node.getChild()) {
			if (nd instanceof LogicalComputingPlatformNode) {
				LogicalComputingPlatformNode pNode = (LogicalComputingPlatformNode) nd;
				content = content + "platform" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + pNode.getName() + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof LogicalComputingNode) {
						LogicalComputingNode cNode = (LogicalComputingNode) cNd;
						content = content + "node" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + cNode.getName() + Constants.FLD_SEP + ((cNode.getAvailMem() != null) ? cNode.getAvailMem() : " ") + Constants.FLD_SEP + ((cNode.getEndianess() != null) ? cNode.getEndianess() : " ") + Constants.FLD_SEP + ((cNode.getMst() != null) ? cNode.getMst() : " ") + Constants.FLD_SEP + ((cNode.getOsName() != null) ? cNode.getOsName() : " ") + Constants.FLD_SEP + ((cNode.getOsVer() != null) ? cNode.getOsVer() : " ") + Constants.ROW_SEP;
						for (tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node gNd : cNode.getChild()) {
							LogicalProcessorsNode gNode = (LogicalProcessorsNode) gNd;
							content = content + "proc" + Constants.DEF_SEP + ((gNode.getId() != null) ? gNode.getId() : " ") + Constants.FLD_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP;
							Rectangle gRect = gNode.getConstraints();
							Point cP = gNode.getAbsolute(new Point(gRect.x, gRect.y), 2);
							content = content + cP.x + Constants.VAL_SEP + cP.y + Constants.VAL_SEP + gRect.height + Constants.VAL_SEP + gRect.width + Constants.FLD_SEP;
							content = content + gNode.getNum() + Constants.FLD_SEP + ((gNode.getType() != null) ? gNode.getType() : " ") + Constants.FLD_SEP + ((gNode.getStepDur() != null) ? gNode.getStepDur() : " ") + Constants.ROW_SEP;
						}
					}
				}
			}
		}
		return content;
	}

	public static String getIntFinalAssemblyEditorContent(tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode node) {
		String content = "";
		content = content + "composite" + Constants.DEF_SEP + ((node.getId() != null) ? node.getId() : " ") + Constants.FLD_SEP + ((node.getName() != null) ? node.getName() : " ") + Constants.FLD_SEP + ((node.getDef() != null) ? node.getDef() : " ") + Constants.ROW_SEP;
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node nd : node.getChild()) {
			if (nd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode) {
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode pNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode) nd;
				content = content + "component" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getType() != null) ? pNode.getType() : " ") + Constants.FLD_SEP + ((pNode.getInst() != null) ? pNode.getInst() : " ") + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode cNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode) cNd;
						content = content + "service" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getIntf() != null) ? cNode.getIntf() : " ") + Constants.ROW_SEP;
					} else if (cNd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode cNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode) cNd;
						content = content + "reference" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getIntf() != null) ? cNode.getIntf() : " ") + Constants.ROW_SEP;
					} else if (cNd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode) {
						tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode cNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode) cNd;
						content = content + "component-prop" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getValue() != null) ? cNode.getValue() : " ") + Constants.FLD_SEP + ((cNode.getType() != null) ? cNode.getType() : " ") + Constants.ROW_SEP;
					}
				}
			} else if (nd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode) {
				tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode pNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositePropertyNode) nd;
				content = content + "composite-prop" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getValue() != null) ? pNode.getValue() : " ") + Constants.FLD_SEP + ((pNode.getType() != null) ? pNode.getType() : " ") + Constants.ROW_SEP;
			}
		}
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link lnk : node.getLinks()) {
			content = content + "link" + Constants.DEF_SEP + ((lnk.getId() != null) ? lnk.getId() : " ") + Constants.FLD_SEP;
			content = content + ((lnk.getRank() != null) ? lnk.getRank() : " ") + Constants.FLD_SEP + ((lnk.getName() != null) ? lnk.getName() : " ") + ((lnk.getbPoints().size() == 0) ? Constants.ROW_SEP : Constants.FLD_SEP);
			for (int i = 0; i < lnk.getbPoints().size(); i++) {
				Point p = lnk.getbPoints().get(i);
				content = content + p.x + Constants.VAL_SEP + p.y + ((i == (lnk.getbPoints().size() - 1)) ? Constants.ROW_SEP : Constants.BP_SEP);
			}
		}
		return content;
	}

	public static String getIntDeploymentEditorContent(DeploymentNode node) {
		String content = "";
		content = content + "deploy" + Constants.DEF_SEP + ((node.getId() != null) ? node.getId() : " ") + Constants.FLD_SEP + ((node.getlSys() != null) ? node.getlSys() : " ") + Constants.FLD_SEP + ((node.getfAssmbl() != null) ? node.getfAssmbl() : " ") + Constants.ROW_SEP;
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node nd : node.getChild()) {
			if (nd instanceof ProtectionDomainNode) {
				ProtectionDomainNode pNode = (ProtectionDomainNode) nd;
				content = content + "protdom" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getEoCompNode() != null) ? pNode.getEoCompNode() : " ") + Constants.FLD_SEP + ((pNode.getEoCompPlatform() != null) ? pNode.getEoCompPlatform() : " ") + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof DeployedModuleInstanceNode) {
						DeployedModuleInstanceNode cNode = (DeployedModuleInstanceNode) cNd;
						content = content + "dmod" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getCompName() != null) ? cNode.getCompName() : " ") + Constants.FLD_SEP + ((cNode.getModuleName() != null) ? cNode.getModuleName() : " ") + Constants.FLD_SEP + ((cNode.getPriority() != null) ? cNode.getPriority() : " ") + Constants.ROW_SEP;
					}
					if (cNd instanceof DeployedTriggerInstanceNode) {
						DeployedTriggerInstanceNode cNode = (DeployedTriggerInstanceNode) cNd;
						content = content + "dtrig" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getCompName() != null) ? cNode.getCompName() : " ") + Constants.FLD_SEP + ((cNode.getTriggerName() != null) ? cNode.getTriggerName() : " ") + Constants.FLD_SEP + ((cNode.getPriority() != null) ? cNode.getPriority() : " ") + Constants.ROW_SEP;
					}
				}
			}
			if (nd instanceof PlatformConfigurationNode) {
				PlatformConfigurationNode pNode = (PlatformConfigurationNode) nd;
				content = content + "platform" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getCompPlatform() != null) ? pNode.getCompPlatform() : "") + Constants.FLD_SEP + ((pNode.getNotifMaxNumber() != null) ? pNode.getNotifMaxNumber() : " ") + Constants.FLD_SEP + ((pNode.getMcastAddr() != null) ? pNode.getMcastAddr() : " ") + Constants.FLD_SEP + ((pNode.getPort() != null) ? pNode.getPort() : " ") + Constants.FLD_SEP + ((pNode.getPlatNum() != null) ? pNode.getPlatNum() : " ") + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof ComputingNodeConfigurationNode) {
						ComputingNodeConfigurationNode cNode = (ComputingNodeConfigurationNode) cNd;
						content = content + "cnc" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getSchedInfo() != null) ? cNode.getSchedInfo() : " ") + Constants.ROW_SEP;
					}
				}
			}
		}
		return content;
	}

	public static String getInitAssemblyEditorContent(CompositeNode node) {
		String content = "";
		content = content + "composite" + Constants.DEF_SEP + ((node.getId() != null) ? node.getId() : " ") + Constants.FLD_SEP + ((node.getName() != null) ? node.getName() : " ") + Constants.ROW_SEP;
		for (Node nd : node.getChild()) {
			if (nd instanceof ComponentNode) {
				ComponentNode pNode = (ComponentNode) nd;
				content = content + "component" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getType() != null) ? pNode.getType() : " ") + Constants.ROW_SEP;
				for (Node cNd : pNode.getChild()) {
					if (cNd instanceof ServiceNode) {
						ServiceNode cNode = (ServiceNode) cNd;
						content = content + "service" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + cNode.getName() + Constants.FLD_SEP + cNode.getIntf() + Constants.ROW_SEP;
					} else if (cNd instanceof ReferenceNode) {
						ReferenceNode cNode = (ReferenceNode) cNd;
						content = content + "reference" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getIntf() != null) ? cNode.getIntf() : " ") + Constants.ROW_SEP;
					} else if (cNd instanceof ComponentPropertyNode) {
						ComponentPropertyNode cNode = (ComponentPropertyNode) cNd;
						content = content + "component-prop" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getValue() != null) ? cNode.getValue() : " ") + Constants.FLD_SEP + ((cNode.getType() != null) ? cNode.getType() : " ") + Constants.ROW_SEP;
					}
				}
			} else if (nd instanceof CompositePropertyNode) {
				CompositePropertyNode pNode = (CompositePropertyNode) nd;
				content = content + "composite-prop" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getValue() != null) ? pNode.getValue() : " ") + Constants.FLD_SEP + ((pNode.getType() != null) ? pNode.getType() : " ") + Constants.ROW_SEP;
			}
		}
		for (Link lnk : node.getLinks()) {
			content = content + "link" + Constants.DEF_SEP + ((lnk.getId() != null) ? lnk.getId() : " ") + Constants.FLD_SEP;
			content = content + ((lnk.getRank() != null) ? lnk.getRank() : " ") + Constants.FLD_SEP + ((lnk.getName() != null) ? lnk.getName() : " ") + ((lnk.getbPoints().size() == 0) ? Constants.ROW_SEP : Constants.FLD_SEP);
			for (int i = 0; i < lnk.getbPoints().size(); i++) {
				Point p = lnk.getbPoints().get(i);
				content = content + p.x + Constants.VAL_SEP + p.y + ((i == (lnk.getbPoints().size() - 1)) ? Constants.ROW_SEP : Constants.BP_SEP);
			}
		}
		return content;
	}

	public static String getCompImplEditorContent(ComponentImplementationNode node) {
		String content = "";
		content = content + "cimp" + Constants.DEF_SEP + ((node.getId() != null) ? node.getId() : " ") + Constants.FLD_SEP + ((node.getName() != null) ? node.getName() : " ") + Constants.ROW_SEP;
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node nd : node.getChild()) {
			if (nd instanceof TriggerInstanceNode) {
				TriggerInstanceNode pNode = (TriggerInstanceNode) nd;
				content = content + "trig" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getPriority() != null) ? pNode.getPriority() : " ") + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof TriggerInstanceTerminalNode) {
						TriggerInstanceTerminalNode cNode = (TriggerInstanceTerminalNode) cNd;
						content = content + "titer" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.ROW_SEP;
					}
				}
			} else if (nd instanceof DynamicTriggerInstanceNode) {
				DynamicTriggerInstanceNode pNode = (DynamicTriggerInstanceNode) nd;
				content = content + "dtrig" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getPriority() != null) ? pNode.getPriority() : " ") + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof DynamicTriggerInstanceTerminalNode) {
						DynamicTriggerInstanceTerminalNode cNode = (DynamicTriggerInstanceTerminalNode) cNd;
						content = content + "dtiter" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getType() != null) ? cNode.getType() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.ROW_SEP;
					}
				}
			} else if (nd instanceof tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) {
				tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode pNode = (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode) nd;
				content = content + "service" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + ((pNode.getIntf() != null) ? pNode.getIntf() : " ") + Constants.FLD_SEP + ((pNode.getType() != null) ? pNode.getType() : " ") + Constants.FLD_SEP;
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof ServiceOperationNode) {
						ServiceOperationNode cNode = (ServiceOperationNode) cNd;
						content = content + "sop" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getType() != null) ? cNode.getType() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getDir() != null) ? cNode.getDir() : " ") + Constants.ROW_SEP;
					}
				}
			} else if (nd instanceof ModuleTypeNode) {
				ModuleTypeNode pNode = (ModuleTypeNode) nd;
				content = content + "mod" + Constants.DEF_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
				content = content + ((pNode.getName() != null) ? pNode.getName() : " ") + Constants.FLD_SEP + pNode.isSup() + Constants.FLD_SEP;
				Rectangle rect = pNode.getConstraints();
				content = content + rect.x + Constants.VAL_SEP + rect.y + Constants.VAL_SEP + rect.height + Constants.VAL_SEP + rect.width + Constants.ROW_SEP;
				for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node cNd : pNode.getChild()) {
					if (cNd instanceof ModuleImplementationNode) {
						ModuleImplementationNode cNode = (ModuleImplementationNode) cNd;
						content = content + "impl" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getType() != null) ? cNode.getType() : " ") + Constants.FLD_SEP + ((cNode.getLang() != null) ? cNode.getLang() : " ") + Constants.ROW_SEP;
					} else if (cNd instanceof ModuleInstanceNode) {
						ModuleInstanceNode cNode = (ModuleInstanceNode) cNd;
						content = content + "inst" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getImpl() != null) ? cNode.getImpl() : " ") + Constants.FLD_SEP + ((cNode.getPriority() != null) ? cNode.getPriority() : " ") + Constants.ROW_SEP;
						for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node gNd : cNode.getChild()) {
							if (gNd instanceof ModuleInstancePropertyNode) {
								ModuleInstancePropertyNode gNode = (ModuleInstancePropertyNode) gNd;
								content = content + "prop" + Constants.DEF_SEP + ((gNode.getId() != null) ? gNode.getId() : " ") + Constants.FLD_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP;
								Rectangle gRect = gNode.getConstraints();
								Point cP = gNode.getAbsolute(new Point(gRect.x, gRect.y), 2);
								content = content + cP.x + Constants.VAL_SEP + cP.y + Constants.VAL_SEP + gRect.height + Constants.VAL_SEP + gRect.width + Constants.FLD_SEP;
								content = content + ((gNode.getName() != null) ? gNode.getName() : " ") + Constants.FLD_SEP + ((gNode.getType() != null) ? gNode.getType() : " ") + Constants.FLD_SEP + ((gNode.getValue() != null) ? gNode.getValue() : " ") + Constants.ROW_SEP;
							}
						}
					} else if (cNd instanceof ModuleOperationNode) {
						ModuleOperationNode cNode = (ModuleOperationNode) cNd;
						content = content + "mop" + Constants.DEF_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP + ((pNode.getId() != null) ? pNode.getId() : " ") + Constants.FLD_SEP;
						Rectangle cRect = cNode.getConstraints();
						Point p = cNode.getAbsolute(new Point(cRect.x, cRect.y), 1);
						content = content + p.x + Constants.VAL_SEP + p.y + Constants.VAL_SEP + cRect.height + Constants.VAL_SEP + cRect.width + Constants.FLD_SEP;
						content = content + ((cNode.getName() != null) ? cNode.getName() : " ") + Constants.FLD_SEP + ((cNode.getType() != null) ? cNode.getType() : " ") + Constants.FLD_SEP + ((cNode.isSync()) ? "true" : "false") + Constants.FLD_SEP + ((cNode.getTimeout() != null) ? cNode.getTimeout() : "0") + Constants.FLD_SEP + ((cNode.getdType() != null) ? cNode.getdType() : " ") + Constants.ROW_SEP;
						for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node gNd : cNode.getChild()) {
							if (gNd instanceof ModuleOperationParameterNode) {
								ModuleOperationParameterNode gNode = (ModuleOperationParameterNode) gNd;
								content = content + "mopp" + Constants.DEF_SEP + ((gNode.getId() != null) ? gNode.getId() : " ") + Constants.FLD_SEP + ((cNode.getId() != null) ? cNode.getId() : " ") + Constants.FLD_SEP;
								Rectangle gRect = gNode.getConstraints();
								Point cP = gNode.getAbsolute(new Point(gRect.x, gRect.y), 2);
								content = content + cP.x + Constants.VAL_SEP + cP.y + Constants.VAL_SEP + gRect.height + Constants.VAL_SEP + gRect.width + Constants.FLD_SEP;
								content = content + ((gNode.getName() != null) ? gNode.getName() : " ") + Constants.FLD_SEP + ((gNode.getType() != null) ? gNode.getType() : " ") + Constants.FLD_SEP + ((gNode.getdType() != null) ? gNode.getdType() : " ") + Constants.ROW_SEP;
							}
						}
					}
				}
			}
		}
		for (tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link lnk : node.getLinks()) {
			content = content + "link" + Constants.DEF_SEP + ((lnk.getId() != null) ? lnk.getId() : " ") + Constants.FLD_SEP;
			content = content + ((lnk.getType() != null) ? lnk.getType() : " ") + Constants.FLD_SEP + ((lnk.getsInst() != null) ? lnk.getsInst() : " ") + Constants.FLD_SEP + ((lnk.gettInst() != null) ? lnk.gettInst() : " ") + Constants.FLD_SEP + ((lnk.getPeriod() != null) ? lnk.getPeriod() : " ") + Constants.FLD_SEP + ((lnk.getName() != null) ? lnk.getName() : " ") + ((lnk.getbPoints().size() == 0) ? Constants.ROW_SEP : Constants.FLD_SEP);
			for (int i = 0; i < lnk.getbPoints().size(); i++) {
				Point p = lnk.getbPoints().get(i);
				content = content + p.x + Constants.VAL_SEP + p.y + ((i == (lnk.getbPoints().size() - 1)) ? Constants.ROW_SEP : Constants.BP_SEP);
			}
		}
		return content;
	}

}

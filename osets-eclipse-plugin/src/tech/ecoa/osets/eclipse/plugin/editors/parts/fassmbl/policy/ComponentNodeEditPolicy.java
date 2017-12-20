/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.policy;

import java.util.ArrayList;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import tech.ecoa.osets.eclipse.plugin.common.CompoundWrapperCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.ComponentNodeDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.ComponentPropertyNodeDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.LinkDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node;

public class ComponentNodeEditPolicy extends ComponentEditPolicy {
	private ArrayList<Link> modifiedLinks;
	private ArrayList<Node> properties;

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof ComponentNode) {
			CompoundWrapperCommand cCmd = new CompoundWrapperCommand();
			ComponentNode cNode = (ComponentNode) node;
			ComponentNodeDeleteCommand cmd = new ComponentNodeDeleteCommand();
			cmd.setNode(cNode);
			cmd.setParent((CompositeNode) cNode.getParent());
			getModifiedlinks(cNode);
			if (modifiedLinks != null && modifiedLinks.size() > 0) {
				for (Link link : modifiedLinks) {
					LinkDeleteCommand lCmd = new LinkDeleteCommand();
					lCmd.setLink(link);
					lCmd.setSource(lCmd.getLink().getSource());
					lCmd.setTarget(lCmd.getLink().getTarget());
					cCmd.add(lCmd);
				}
			}
			getProperties(cNode);
			if (properties != null && properties.size() > 0) {
				for (Node child : properties) {
					ComponentPropertyNodeDeleteCommand pCmd = new ComponentPropertyNodeDeleteCommand();
					pCmd.setNode((ComponentPropertyNode) child);
					pCmd.setParent(cNode);
					cCmd.add(pCmd);
				}
			}
			cCmd.add(cmd);
			ret = cCmd;
		}
		return ret;
	}

	private void getModifiedlinks(ComponentNode node) {
		modifiedLinks = new ArrayList<Link>();
		CompositeNode root = node.getCompositeNode();
		for (Node child : node.getChild()) {
			for (Link link : root.getLinks()) {
				if (link.getSource().equals(child) || link.getTarget().equals(child)) {
					modifiedLinks.add(link);
				}
			}
		}
	}

	private void getProperties(ComponentNode node) {
		properties = new ArrayList<Node>();
		for (Node child : node.getChild()) {
			if (child instanceof ComponentPropertyNode)
				properties.add(child);
		}
	}
}

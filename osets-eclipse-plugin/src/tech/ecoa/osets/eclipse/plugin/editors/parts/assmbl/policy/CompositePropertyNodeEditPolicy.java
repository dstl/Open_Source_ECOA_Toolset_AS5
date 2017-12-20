/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.commands.CompositePropertyNodeDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.CompositePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.Node;

public class CompositePropertyNodeEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof CompositePropertyNode) {
			CompositePropertyNode pNode = (CompositePropertyNode) node;
			CompositePropertyNodeDeleteCommand cmd = new CompositePropertyNodeDeleteCommand();
			cmd.setNode(pNode);
			cmd.setParent((CompositeNode) pNode.getParent());
			ret = cmd;
		}
		return ret;
	}

}

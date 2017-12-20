/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.ComponentPropertyNodeDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ComponentPropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node;

public class ComponentPropertyNodeEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof ComponentPropertyNode) {
			ComponentPropertyNode pNode = (ComponentPropertyNode) node;
			ComponentPropertyNodeDeleteCommand cmd = new ComponentPropertyNodeDeleteCommand();
			cmd.setNode(pNode);
			cmd.setParent((ComponentNode) pNode.getParent());
			ret = cmd;
		}
		return ret;
	}

}

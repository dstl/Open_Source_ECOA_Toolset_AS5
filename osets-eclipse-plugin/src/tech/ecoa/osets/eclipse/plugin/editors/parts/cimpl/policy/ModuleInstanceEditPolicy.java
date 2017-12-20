/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ModuleInstanceDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;

public class ModuleInstanceEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof ModuleInstanceNode) {
			ModuleInstanceNode pNode = (ModuleInstanceNode) node;
			ModuleInstanceDeleteCommand cmd = new ModuleInstanceDeleteCommand();
			cmd.setNode(pNode);
			cmd.setParent((ModuleTypeNode) pNode.getParent());
			ret = cmd;
		}
		return ret;
	}

}

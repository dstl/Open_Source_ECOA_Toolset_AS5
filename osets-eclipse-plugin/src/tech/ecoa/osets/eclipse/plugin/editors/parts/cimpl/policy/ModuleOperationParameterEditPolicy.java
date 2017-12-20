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

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.commands.ModuleOperationParameterDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationParameterNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Node;

public class ModuleOperationParameterEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof ModuleOperationParameterNode) {
			ModuleOperationParameterNode pNode = (ModuleOperationParameterNode) node;
			ModuleOperationParameterDeleteCommand cmd = new ModuleOperationParameterDeleteCommand();
			cmd.setNode(pNode);
			cmd.setParent((ModuleOperationNode) pNode.getParent());
			ret = cmd;
		}
		return ret;
	}

}

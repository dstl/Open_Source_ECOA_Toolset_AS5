/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.commands.DeployedModuleInstanceDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainNode;

public class DeployedModuleInstanceEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof DeployedModuleInstanceNode) {
			DeployedModuleInstanceNode pNode = (DeployedModuleInstanceNode) node;
			DeployedModuleInstanceDeleteCommand cmd = new DeployedModuleInstanceDeleteCommand();
			cmd.setNode(pNode);
			cmd.setParent((ProtectionDomainNode) pNode.getParent());
			ret = cmd;
		}
		return ret;
	}

}

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

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.commands.ComputingNodeConfigurationDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ComputingNodeConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;

public class ComputingNodeConfigurationEditPolicy extends ComponentEditPolicy {
	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof ComputingNodeConfigurationNode) {
			ComputingNodeConfigurationNode pNode = (ComputingNodeConfigurationNode) node;
			ComputingNodeConfigurationDeleteCommand cmd = new ComputingNodeConfigurationDeleteCommand();
			cmd.setNode(pNode);
			cmd.setParent((PlatformConfigurationNode) pNode.getParent());
			ret = cmd;
		}
		return ret;
	}

}

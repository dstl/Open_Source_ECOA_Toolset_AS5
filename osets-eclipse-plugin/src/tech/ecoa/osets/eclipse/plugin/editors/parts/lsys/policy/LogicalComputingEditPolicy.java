/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ComponentEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import tech.ecoa.osets.eclipse.plugin.common.CompoundWrapperCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.commands.LogicalComputingDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingPlatformNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node;

public class LogicalComputingEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof LogicalComputingNode) {
			CompoundWrapperCommand cCmd = new CompoundWrapperCommand();
			LogicalComputingNode cNode = (LogicalComputingNode) node;
			LogicalComputingDeleteCommand cmd = new LogicalComputingDeleteCommand();
			cmd.setNode(cNode);
			cmd.setParent((LogicalComputingPlatformNode) cNode.getParent());
			cCmd.add(cmd);
			ret = cCmd;
		}
		return ret;
	}
}

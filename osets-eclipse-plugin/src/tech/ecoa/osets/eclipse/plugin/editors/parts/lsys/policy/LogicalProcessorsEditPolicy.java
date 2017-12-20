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
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.commands.LogicalProcessorsDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalProcessorsNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.Node;

public class LogicalProcessorsEditPolicy extends ComponentEditPolicy {

	@Override
	protected Command createDeleteCommand(GroupRequest deleteRequest) {
		Command ret = null;
		Node node = (Node) getHost().getModel();
		if (node instanceof LogicalProcessorsNode) {
			CompoundWrapperCommand cCmd = new CompoundWrapperCommand();
			LogicalProcessorsNode cNode = (LogicalProcessorsNode) node;
			LogicalProcessorsDeleteCommand cmd = new LogicalProcessorsDeleteCommand();
			cmd.setNode(cNode);
			cmd.setParent((LogicalComputingNode) cNode.getParent());
			cCmd.add(cmd);
			ret = cCmd;
		}
		return ret;
	}
}

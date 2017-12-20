/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.LinkCreateCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Node;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ReferenceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.ServiceNode;

public class LinkCreatePolicy extends GraphicalNodeEditPolicy {

	@Override
	protected Command getConnectionCompleteCommand(CreateConnectionRequest request) {
		LinkCreateCommand cmd = (LinkCreateCommand) request.getStartCommand();
		Node end = (Node) getHost().getModel();
		Node start = cmd.getSource();
		if (end instanceof ReferenceNode && (!(start.getParent().getId().equalsIgnoreCase(end.getParent().getId())))) {
			ReferenceNode nd = (ReferenceNode) end;
			ServiceNode st = (ServiceNode) start;
			if (nd.getIntf().equalsIgnoreCase(st.getIntf())) {
				cmd.setTarget(end);
				return cmd;
			}
		}
		return null;
	}

	@Override
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		Node start = (Node) getHost().getModel();
		if (start instanceof ServiceNode) {
			LinkCreateCommand cmd = new LinkCreateCommand();
			cmd.setSource(start);
			cmd.setLink((Link) request.getNewObject());
			request.setStartCommand(cmd);
			return cmd;
		}
		return null;
	}

	@Override
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}

	@Override
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}

}

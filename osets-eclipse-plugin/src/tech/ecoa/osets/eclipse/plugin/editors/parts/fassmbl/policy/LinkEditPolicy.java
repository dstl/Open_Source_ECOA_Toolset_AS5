/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.policy;

import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.ConnectionEditPolicy;
import org.eclipse.gef.requests.GroupRequest;

import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.commands.LinkDeleteCommand;
import tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model.Link;

public class LinkEditPolicy extends ConnectionEditPolicy {

	@Override
	protected Command getDeleteCommand(GroupRequest request) {
		LinkDeleteCommand cmd = new LinkDeleteCommand();
		cmd.setLink((Link) getHost().getModel());
		cmd.setSource(cmd.getLink().getSource());
		cmd.setTarget(cmd.getLink().getTarget());
		return cmd;
	}

}

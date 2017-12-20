/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.edpart;

import org.eclipse.gef.EditPart;

import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalComputingPlatformNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalProcessorsNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.lsys.model.LogicalSystemNode;

public class EditPartFactory implements org.eclipse.gef.EditPartFactory {

	private String containerName;

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof LogicalSystemNode)
			return new LogicalSystemEditPart((LogicalSystemNode) model, containerName);
		else if (model instanceof LogicalComputingPlatformNode)
			return new LogicalComputingPlatformEditPart((LogicalComputingPlatformNode) model, containerName);
		else if (model instanceof LogicalComputingNode)
			return new LogicalComputingEditPart((LogicalComputingNode) model, containerName);
		else if (model instanceof LogicalProcessorsNode)
			return new LogicalProcessorsEditPart((LogicalProcessorsNode) model, containerName);
		return null;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}

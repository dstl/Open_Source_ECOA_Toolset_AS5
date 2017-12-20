/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.edpart;

import org.eclipse.gef.EditPart;

import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ComponentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ComponentPropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.CompositeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.CompositePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ReferenceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model.ServiceNode;

public class EditPartFactory implements org.eclipse.gef.EditPartFactory {

	private String containerName;

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof CompositeNode)
			return new CompositeEditPart((CompositeNode) model, containerName);
		else if (model instanceof CompositePropertyNode)
			return new CompositePropertyEditPart((CompositePropertyNode) model, containerName);
		else if (model instanceof ComponentNode)
			return new ComponentEditPart((ComponentNode) model, containerName);
		else if (model instanceof ComponentPropertyNode)
			return new ComponentPropertyEditPart((ComponentPropertyNode) model, containerName);
		else if (model instanceof ServiceNode)
			return new ServiceEditPart((ServiceNode) model, containerName);
		else if (model instanceof ReferenceNode)
			return new ReferenceEditPart((ReferenceNode) model, containerName);
		else if (model instanceof Link)
			return new LinkEditPart((Link) model, containerName);
		return null;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}

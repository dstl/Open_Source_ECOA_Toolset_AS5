/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.edpart;

import org.eclipse.gef.EditPart;

import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ComponentImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.DynamicTriggerInstanceTerminalNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.Link;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleImplementationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleInstancePropertyNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleOperationParameterNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ModuleTypeNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.ServiceOperationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model.TriggerInstanceTerminalNode;

public class EditPartFactory implements org.eclipse.gef.EditPartFactory {

	private String containerName;

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof ComponentImplementationNode)
			return new ComponentImplementationEditPart((ComponentImplementationNode) model, containerName);
		else if (model instanceof DynamicTriggerInstanceNode)
			return new DynamicTriggerInstanceEditPart((DynamicTriggerInstanceNode) model, containerName);
		else if (model instanceof DynamicTriggerInstanceTerminalNode)
			return new DynamicTriggerInstanceTerminalEditPart((DynamicTriggerInstanceTerminalNode) model, containerName);
		else if (model instanceof TriggerInstanceNode)
			return new TriggerInstanceEditPart((TriggerInstanceNode) model, containerName);
		else if (model instanceof TriggerInstanceTerminalNode)
			return new TriggerInstanceTerminalEditPart((TriggerInstanceTerminalNode) model, containerName);
		else if (model instanceof ModuleImplementationNode)
			return new ModuleImplementationEditPart((ModuleImplementationNode) model, containerName);
		else if (model instanceof ModuleInstanceNode)
			return new ModuleInstanceEditPart((ModuleInstanceNode) model, containerName);
		else if (model instanceof ModuleInstancePropertyNode)
			return new ModuleInstancePropertyEditPart((ModuleInstancePropertyNode) model, containerName);
		else if (model instanceof ModuleOperationNode)
			return new ModuleOperationEditPart((ModuleOperationNode) model, containerName);
		else if (model instanceof ModuleOperationParameterNode)
			return new ModuleOperationParameterEditPart((ModuleOperationParameterNode) model, containerName);
		else if (model instanceof ModuleTypeNode)
			return new ModuleTypeEditPart((ModuleTypeNode) model, containerName);
		else if (model instanceof ServiceNode)
			return new ServiceEditPart((ServiceNode) model, containerName);
		else if (model instanceof ServiceOperationNode)
			return new ServiceOperationEditPart((ServiceOperationNode) model, containerName);
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

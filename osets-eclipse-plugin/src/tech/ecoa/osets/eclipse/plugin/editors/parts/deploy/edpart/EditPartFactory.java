/*
 * Copyright 2017, BAE Systems Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.edpart;

import org.eclipse.gef.EditPart;

import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ComputingNodeConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedModuleInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeployedTriggerInstanceNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.DeploymentNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.PlatformConfigurationNode;
import tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model.ProtectionDomainNode;

public class EditPartFactory implements org.eclipse.gef.EditPartFactory {

	private String containerName;

	@Override
	public EditPart createEditPart(EditPart context, Object model) {
		if (model instanceof DeploymentNode)
			return new DeploymentEditPart((DeploymentNode) model, containerName);
		else if (model instanceof PlatformConfigurationNode)
			return new PlatformConfigurationEditPart((PlatformConfigurationNode) model, containerName);
		else if (model instanceof ComputingNodeConfigurationNode)
			return new ComputingNodeConfigurationEditPart((ComputingNodeConfigurationNode) model, containerName);
		else if (model instanceof ProtectionDomainNode)
			return new ProtectionDomainEditPart((ProtectionDomainNode) model, containerName);
		else if (model instanceof DeployedModuleInstanceNode)
			return new DeployedModuleInstanceEditPart((DeployedModuleInstanceNode) model, containerName);
		else if (model instanceof DeployedTriggerInstanceNode)
			return new DeployedTriggerInstanceEditPart((DeployedTriggerInstanceNode) model, containerName);
		return null;
	}

	public String getContainerName() {
		return containerName;
	}

	public void setContainerName(String containerName) {
		this.containerName = containerName;
	}

}

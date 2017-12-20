package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class DeployedModuleInstanceCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		DeployedModuleInstanceNode node = new DeployedModuleInstanceNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return DeployedModuleInstanceNode.class;
	}

}

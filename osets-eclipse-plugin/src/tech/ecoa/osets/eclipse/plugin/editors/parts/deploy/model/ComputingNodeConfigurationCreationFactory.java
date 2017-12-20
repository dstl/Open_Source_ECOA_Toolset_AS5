package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ComputingNodeConfigurationCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ComputingNodeConfigurationNode node = new ComputingNodeConfigurationNode();
		node.setId(UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ComputingNodeConfigurationNode.class;
	}

}

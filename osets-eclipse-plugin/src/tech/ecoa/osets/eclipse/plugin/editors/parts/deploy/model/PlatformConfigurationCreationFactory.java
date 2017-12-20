package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class PlatformConfigurationCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		PlatformConfigurationNode node = new PlatformConfigurationNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return PlatformConfigurationNode.class;
	}

}

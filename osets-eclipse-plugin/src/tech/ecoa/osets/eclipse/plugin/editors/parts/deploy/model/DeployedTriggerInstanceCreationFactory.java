package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class DeployedTriggerInstanceCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		DeployedTriggerInstanceNode node = new DeployedTriggerInstanceNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return DeployedTriggerInstanceNode.class;
	}

}

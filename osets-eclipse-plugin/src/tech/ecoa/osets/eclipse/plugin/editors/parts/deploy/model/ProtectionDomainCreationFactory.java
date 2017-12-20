package tech.ecoa.osets.eclipse.plugin.editors.parts.deploy.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ProtectionDomainCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ProtectionDomainNode node = new ProtectionDomainNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ProtectionDomainNode.class;
	}

}

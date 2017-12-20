package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ServiceNodeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ServiceNode node = new ServiceNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ServiceNode.class;
	}

}

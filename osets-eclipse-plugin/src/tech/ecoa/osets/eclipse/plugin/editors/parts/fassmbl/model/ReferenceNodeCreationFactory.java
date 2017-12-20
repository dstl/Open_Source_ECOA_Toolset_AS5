package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ReferenceNodeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ReferenceNode node = new ReferenceNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ReferenceNode.class;
	}

}

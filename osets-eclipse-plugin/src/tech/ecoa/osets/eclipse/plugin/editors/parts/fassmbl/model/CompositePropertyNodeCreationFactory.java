package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class CompositePropertyNodeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		CompositePropertyNode node = new CompositePropertyNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return CompositePropertyNode.class;
	}

}

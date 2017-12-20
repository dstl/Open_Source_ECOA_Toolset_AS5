package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ComponentPropertyNodeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ComponentPropertyNode node = new ComponentPropertyNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ComponentPropertyNode.class;
	}

}

package tech.ecoa.osets.eclipse.plugin.editors.parts.assmbl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ComponentNodeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ComponentNode node = new ComponentNode();
		node.setId("" + UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ComponentNode.class;
	}

}

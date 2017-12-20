package tech.ecoa.osets.eclipse.plugin.editors.parts.cimpl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class ModuleTypeCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		ModuleTypeNode node = new ModuleTypeNode();
		node.setId(UUID.randomUUID().toString());
		return node;
	}

	@Override
	public Object getObjectType() {
		return ModuleTypeNode.class;
	}

}

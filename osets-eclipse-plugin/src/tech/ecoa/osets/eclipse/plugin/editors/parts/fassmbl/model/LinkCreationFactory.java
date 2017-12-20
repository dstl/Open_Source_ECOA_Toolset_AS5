package tech.ecoa.osets.eclipse.plugin.editors.parts.fassmbl.model;

import java.util.UUID;

import org.eclipse.gef.requests.CreationFactory;

public class LinkCreationFactory implements CreationFactory {

	@Override
	public Object getNewObject() {
		Link link = new Link();
		link.setName(UUID.randomUUID().toString());
		return link;
	}

	@Override
	public Object getObjectType() {
		return Link.class;
	}

}

package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.DefaultContentNode;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.IContentNodeDefinition;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

class ContentNodeInternalBuilder extends AbstractKeyedInternalBuilder<IContentNodeDefinition> {

	public ContentNodeInternalBuilder(IContentNodeDefinition entityDefintion) {
		super(entityDefintion);
	}

	@Override
	public IMutableEntity<IContentNodeDefinition> build(
			ContentRetriever contentRetriever, IMutableEntity<?> parent) {
		DefaultContentNode retval = new DefaultContentNode(getEntityDefinition(), getKey(), contentRetriever, parent);
		return retval;
	}
	
}
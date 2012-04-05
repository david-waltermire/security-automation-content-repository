package gov.nist.scap.content.semantic.entity;

import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IMutableEntity;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;

public interface IInternalBuilder {
	IMutableEntity<?> build(IKey key, ContentRetriever contentRetriever, IContainer<?> parent);
}

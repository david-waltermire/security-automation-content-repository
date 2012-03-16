package org.scapdev.content.core.persistence.semantic.translation;

import gov.nist.scap.content.model.IEntity;

import org.openrdf.model.URI;

public interface EntityMetadataMap {

    URI getResourceURI(IEntity<?> entity);
    String getContentId(IEntity<?> entity);
}

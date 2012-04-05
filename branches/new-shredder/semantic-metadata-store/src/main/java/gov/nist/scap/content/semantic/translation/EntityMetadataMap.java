package gov.nist.scap.content.semantic.translation;

import gov.nist.scap.content.model.IEntity;

import org.openrdf.model.URI;

public interface EntityMetadataMap {

    /**
     * get the resource URI of an entity
     * @param entity the entity of interest
     * @return the URI of the entity
     */
    URI getResourceURI(IEntity<?> entity);
    /**
     * get the content ID of an entity
     * @param entity the entity of interest
     * @return the content ID of the entity
     */
    String getContentId(IEntity<?> entity);
}

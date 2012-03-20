package org.scapdev.content.core.persistence.semantic;

import org.openrdf.repository.Repository;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;

public interface IPersistenceContext {

    Repository getRepository();
    MetaDataOntology getOntology();
    ContentRetrieverFactory getContentRetrieverFactory();
    
}

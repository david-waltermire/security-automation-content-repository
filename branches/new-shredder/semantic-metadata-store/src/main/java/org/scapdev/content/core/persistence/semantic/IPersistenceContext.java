package org.scapdev.content.core.persistence.semantic;

import org.openrdf.repository.Repository;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;

public interface IPersistenceContext {

    /**
     * Get the repository within context
     * @return the repository
     */
    Repository getRepository();
    /**
     * Get the ontology within context
     * @return the ontology
     */
    MetaDataOntology getOntology();
    /**
     * Get the content retriever factory within context
     * @return the content retriever factory
     */
    ContentRetrieverFactory getContentRetrieverFactory();
    
}

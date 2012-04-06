package gov.nist.scap.content.semantic;

import org.openrdf.repository.Repository;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;

/**
 * A collection of necessary objects to interact with the triple store and
 * content store in a meaningful manner
 * 
 * @author Adam Halbardier
 */
public interface IPersistenceContext {

    /**
     * Get the repository within context
     * 
     * @return the repository
     */
    Repository getRepository();

    /**
     * Get the ontology within context
     * 
     * @return the ontology
     */
    MetaDataOntology getOntology();

    /**
     * Get the content retriever factory within context
     * 
     * @return the content retriever factory
     */
    ContentRetrieverFactory getContentRetrieverFactory();

}

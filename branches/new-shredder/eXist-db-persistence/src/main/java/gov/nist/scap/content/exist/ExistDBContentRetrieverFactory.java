package gov.nist.scap.content.exist;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;

/**
 * Used to generate eXist-db content retrievers
 * @author Adam Halbardier
 *
 */
public class ExistDBContentRetrieverFactory implements ContentRetrieverFactory {

    private ExistDBContentStore store;
    private ExistDBContentRetrieverFactory(ExistDBContentStore store) {
        this.store = store;
    }
    
    @Override
    public ContentRetriever newContentRetriever(
            String contentId) {
        return store.getContentRetriever(contentId);
    }
    
    /**
     * get a new eXist-db retriever factory
     * @param store the eXist-db content store
     * @return a new eXist-db retriever factory
     */
    public static ExistDBContentRetrieverFactory getInstance(ExistDBContentStore store) {
        return new ExistDBContentRetrieverFactory(store);
    }
    
    
}

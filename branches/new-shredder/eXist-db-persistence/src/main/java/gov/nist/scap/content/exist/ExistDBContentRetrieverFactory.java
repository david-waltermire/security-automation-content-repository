package gov.nist.scap.content.exist;

import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;

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
    
    public static ExistDBContentRetrieverFactory getInstance(ExistDBContentStore store) {
        return new ExistDBContentRetrieverFactory(store);
    }
    
    
}

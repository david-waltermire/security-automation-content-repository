package gov.nist.scap.content.server;

import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.scapdev.content.core.persistence.hybrid.HybridContentPersistenceManager;
import org.scapdev.content.core.persistence.hybrid.HybridContentPersistenceManagerFactory;
import org.scapdev.content.core.persistence.hybrid.MetadataStore;

public class Server {

    public Server(ContentStore contentStore, MetadataStore metadataStore) {
        @SuppressWarnings("unused")
		HybridContentPersistenceManager hcpm =
            HybridContentPersistenceManagerFactory.getInstance().newContentPersistenceManager(
                metadataStore,
                contentStore);
        
        

    }
}
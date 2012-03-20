package gov.nist.scap.content.metadata.tripstore;

import gov.nist.scap.content.model.IEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlObject;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentStore;

public class MockContentStore implements ContentStore {

    @Override
    public XmlObject getContent(String contentId) {
        return null;
    }
    
    @Override
    public ContentRetriever getContentRetriever(String contentId) {
        return null;
    }
    
    @Override
    public Map<String, IEntity<?>> persist(
            Collection<? extends IEntity<?>> entities) {
        Map<String, IEntity<?>> resultMap = new HashMap<String, IEntity<?>>();
        for( IEntity<?> e : entities ) {
            resultMap.put(Long.toString(Math.round(Math.random() * 1000000000)), e);
        }
        return resultMap;
    }
    
    public void shutdown() {};
}

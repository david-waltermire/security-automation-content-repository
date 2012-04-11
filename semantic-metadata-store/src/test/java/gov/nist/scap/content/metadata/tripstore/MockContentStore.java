package gov.nist.scap.content.metadata.tripstore;

import gov.nist.scap.content.model.IEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlObject;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentStore;

/**
 * An in memory implementation of content store that's used to test the triple
 * store without having a dependency on a real content store.
 * 
 * @author Adam Halbardier
 */
public class MockContentStore implements ContentStore {

    Map<String, IEntity<?>> resultMap = new HashMap<String, IEntity<?>>();

    @Override
    public XmlObject getContent(String contentId) {
        return resultMap.get(contentId).getContentHandle().getCursor().getObject();
    }

    @Override
    public ContentRetriever getContentRetriever(String contentId) {
        return new InternalContentRetriver(
            resultMap.get(contentId).getContentHandle().getCursor());
    }

    @Override
    public Map<String, IEntity<?>> persist(
            Collection<? extends IEntity<?>> entities) {
        for (IEntity<?> e : entities) {
            resultMap.put(
                Long.toString(Math.round(Math.random() * 1000000000)),
                e);
        }
        return resultMap;
    }

    @Override
    public ContentRetriever newContentRetriever(String contentId) {
        return new InternalContentRetriver(
            resultMap.get(contentId).getContentHandle().getCursor());
    }

    @Override
    public boolean commit(Object session) {
        // TODO Auto-generated method stub
        return true;
    }
    
    @Override
    public Map<String, IEntity<?>> persist(
            Collection<? extends IEntity<?>> entities,
            Object session) {
        return persist(entities);
    }
    
    @Override
    public boolean rollback(Object session) {
        return true;
    }
    
    @Override
    public void shutdown() {
    };

    private class InternalContentRetriver implements ContentRetriever {
        private XmlCursor xc;

        public InternalContentRetriver(XmlCursor xc) {
            this.xc = xc;
        }

        @Override
        public XmlCursor getCursor() {
            return this.xc;
        }
    }
}

package gov.nist.scap.content.exist;

import gov.nist.scap.content.model.IEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;

import org.apache.xmlbeans.XmlObject;
import org.exist.xmldb.DatabaseImpl;
import org.exist.xmldb.DatabaseInstanceManager;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;

/**
 * An eXist-db implementation of ContentStore
 * 
 * @author Adam Halbardier
 */
public class ExistDBContentStore implements ContentStore {

    private final String URI;
    private final String COLLECTION;
    private final String USERNAME;
    private final String PASSWORD;

    private final ContentRetrieverFactory contentRetrieverFactory;

    private Collection col = null;

    private Map<Object, Set<String>> commitMap =
        new HashMap<Object, Set<String>>();

    /**
     * default constructor
     * 
     * @throws XMLDBException error with the database
     */
    public ExistDBContentStore() throws XMLDBException {
        this("dsig", "content-repo", "test123");
    }

    /**
     * constructor where the collection name can be specified
     * @param collection the collection name to use
     * @throws XMLDBException error with the repo
     */
    public ExistDBContentStore(String collection) throws XMLDBException {
        this(collection, "content-repo", "test123");
    }

    /**
     * constructor where the username/password can be specified
     * @param username the username to access the repo
     * @param password the password for the username
     * @throws XMLDBException error with the repo
     */
    public ExistDBContentStore(String username, String password) throws XMLDBException {
        this("dsig", username, password);
    }

    /**
     * constructor where the username/password and collection can be specified
     * @param collection the collection to use
     * @param username the username
     * @param password the password
     * @throws XMLDBException error with the repo
     */
    public ExistDBContentStore(String collection, String username, String password) throws XMLDBException {
        COLLECTION = collection;
        USERNAME = username;
        PASSWORD = password;
        URI = "xmldb:exist:///db/";

        Database database = new DatabaseImpl();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        col = getOrCreateCollection(COLLECTION);
        col.setProperty(OutputKeys.INDENT, "no");

        contentRetrieverFactory =
            ExistDBContentRetrieverFactory.getInstance(this);
    }

    /**
     * constructor where the username/password and collection can be specified
     * @param collection the collection to use
     * @param username the username
     * @param password the password
     * @param uri the connection URI (must end in "db/")
     * @throws XMLDBException error with the repo
     */
    public ExistDBContentStore(String collection, String username, String password, String uri) throws XMLDBException {
        COLLECTION = collection;
        USERNAME = username;
        PASSWORD = password;
        URI = uri;

        Database database = new DatabaseImpl();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        col = getOrCreateCollection(COLLECTION);
        col.setProperty(OutputKeys.INDENT, "no");

        contentRetrieverFactory =
            ExistDBContentRetrieverFactory.getInstance(this);
    }

    @Override
    public XmlObject getContent(String contentId) {
        if (contentId == null)
            return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public LinkedHashMap<String, IEntity<?>> persist(
            List<? extends IEntity<?>> entities)
            throws ContentException {
        return persist(entities, null);
    }

    @Override
    public LinkedHashMap<String, IEntity<?>> persist(
            List<? extends IEntity<?>> entities,
            Object session) throws ContentException {
        LinkedHashMap<String, IEntity<?>> resultResult =
            new LinkedHashMap<String, IEntity<?>>();
        ExistDBPersistEntityVisitor visitor = new ExistDBPersistEntityVisitor(col, resultResult);
        for (IEntity<?> ie : entities) {
        	ie.accept(visitor);
        }

        if (session != null) {
            commitMap.put(
                session,
                Collections.unmodifiableSet(resultResult.keySet()));
        }
        return resultResult;

    }

    @Override
    public boolean commit(Object session) {
        return commitMap.remove(session) != null;
    }

    @Override
    public boolean rollback(Object session) {
        Set<String> keys = commitMap.remove(session);
        if (keys != null) {
            try {
                for (String key : keys) {
                    col.removeResource(col.getResource(key));
                }
                return true;
            } catch (XMLDBException e1) {
                e1.printStackTrace();
                return false;
            }
        }
        return false;
    }

    @Override
    public ContentRetriever getContentRetriever(String contentId) {
        if (contentId == null)
            return null;
        return new ExistDBContentRetriever(col, contentId);
    }

    @Override
    public void shutdown() {
        if (col != null) {
            try {
                DatabaseInstanceManager manager =
                    (DatabaseInstanceManager)col.getService(
                        "DatabaseInstanceManager",
                        "1.0");
                manager.shutdown();
            } catch (XMLDBException xe) {
                xe.printStackTrace();
            }
        }
    }

    private Collection getOrCreateCollection(String collectionUri)
            throws XMLDBException {
        return getOrCreateCollection(collectionUri, 0);
    }

    private Collection getOrCreateCollection(
            String collectionUri,
            int pathSegmentOffset) throws XMLDBException {

        Collection col =
            DatabaseManager.getCollection(
                URI + collectionUri,
                USERNAME,
                PASSWORD);
        if (col == null) {
            if (collectionUri.startsWith("/")) {
                collectionUri = collectionUri.substring(1);
            }

            String pathSegments[] = collectionUri.split("/");
            if (pathSegments.length > 0) {

                StringBuilder path = new StringBuilder();
                for (int i = 0; i <= pathSegmentOffset; i++) {
                    path.append("/" + pathSegments[i]);
                }

                Collection start =
                    DatabaseManager.getCollection(
                        URI + path,
                        USERNAME,
                        PASSWORD);
                if (start == null) {
                    // collection does not exist, so create
                    String parentPath =
                        path.substring(0, path.lastIndexOf("/"));
                    Collection parent =
                        DatabaseManager.getCollection(
                            URI + parentPath,
                            USERNAME,
                            PASSWORD);
                    CollectionManagementService mgt =
                        (CollectionManagementService)parent.getService(
                            "CollectionManagementService",
                            "1.0");
                    col = mgt.createCollection(pathSegments[pathSegmentOffset]);
                    col.close();
                    parent.close();
                } else {
                    start.close();
                }
            }
            return getOrCreateCollection(collectionUri, ++pathSegmentOffset);
        }
        return col;
    }

    @Override
    public ContentRetriever newContentRetriever(String contentId) {
        return contentRetrieverFactory.newContentRetriever(contentId);
    }

}

package gov.nist.scap.content.exist;

import gov.nist.scap.content.model.IEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.exist.xmldb.DatabaseImpl;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.EXistResource;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

/**
 * An eXist-db implementation of ContentStore
 * 
 * @author Adam Halbardier
 */
public class ExistDBContentStore implements ContentStore {

    private static final String URI = "xmldb:exist:///db/";
    private final String COLLECTION;
    private final String USERNAME;
    private final String PASSWORD;
    public static final String WRAPPER_ELEMENT = "abcdefghijklm";

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
    public Map<String, IEntity<?>> persist(
            java.util.Collection<? extends IEntity<?>> entities)
            throws ContentException {
        return persist(entities, null);
    }

    @Override
    public Map<String, IEntity<?>> persist(
            java.util.Collection<? extends IEntity<?>> entities,
            Object session) throws ContentException {
        XMLResource res = null;
        String resId = null;
        Map<String, IEntity<?>> resultResult =
            new HashMap<String, IEntity<?>>();
        for (IEntity<?> ie : entities) {
            try {
                res = (XMLResource)col.createResource(null, "XMLResource");
                XmlOptions xo = new XmlOptions();
                xo.setSaveOuter();
                XmlCursor xc = ie.getContentHandle().getCursor();
                TokenType tt = xc.toNextToken();
                StringBuilder sbNS = new StringBuilder();
                sbNS.append("<" + WRAPPER_ELEMENT);
                while (tt == TokenType.ATTR || tt == TokenType.NAMESPACE) {
                    if (tt == TokenType.NAMESPACE) {
                        QName q = xc.getName();
                        String spacer = "";
                        if (q.getLocalPart() != null
                            && !q.getLocalPart().equals("")) {
                            spacer = ":" + q.getLocalPart();
                        }
                        sbNS.append(" xmlns" + spacer + "=\""
                            + q.getNamespaceURI() + "\"");
                    }
                    tt = xc.toNextToken();
                }
                sbNS.append(">");
                xc = ie.getContentHandle().getCursor();
                res.setContent(sbNS.toString() + xc.getObject().xmlText(xo)
                    + "</" + WRAPPER_ELEMENT + ">");
                col.storeResource(res);
                resId = res.getId();
                resultResult.put(resId, ie);
                xc.removeXml();
                xc.beginElement("xinclude", "gov:nist:scap:content-repo");
                xc.insertAttributeWithValue("resource-id", resId);
            } catch (XMLDBException e) {
                // TODO: log exception
                // back out all inserted info
                for (String localResId : resultResult.keySet()) {
                    try {
                        col.removeResource(col.getResource(localResId));
                    } catch (XMLDBException e1) {
                        throw new ContentException(
                            "Error rolling back transaction. Database may have stale data!!!",
                            e);
                    }
                }
                throw new ContentException("error persisting content", e);
            } finally {
                // dont forget to cleanup
                if (res != null) {
                    try {
                        ((EXistResource)res).freeResources();
                    } catch (XMLDBException xe) {
                        xe.printStackTrace();
                    }
                }
            }
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

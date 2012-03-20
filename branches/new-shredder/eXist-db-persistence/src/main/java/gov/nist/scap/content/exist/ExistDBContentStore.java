package gov.nist.scap.content.exist;

import gov.nist.scap.content.model.IEntity;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.OutputKeys;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.exist.xmldb.DatabaseInstanceManager;
import org.exist.xmldb.EXistResource;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

public class ExistDBContentStore implements ContentStore {

    private static final String URI = "xmldb:exist:///db/";
    private static final String COLLECTION = "dsig";
    private static final String USERNAME = "content-repo";
    private static final String PASSWORD = "test123";
    public static final String WRAPPER_ELEMENT = "abcdefghijklm";

    private final ContentRetrieverFactory contentRetrieverFactory;
    
    private Collection col = null;

    public ExistDBContentStore()
            throws ClassNotFoundException, XMLDBException,
            InstantiationException, IllegalAccessException {
        final String driver = "org.exist.xmldb.DatabaseImpl";
        // initialize database driver
        Class<?> cl = Class.forName(driver);
        Database database = (Database)cl.newInstance();
        database.setProperty("create-database", "true");
        DatabaseManager.registerDatabase(database);

        col = getOrCreateCollection(COLLECTION);
        col.setProperty(OutputKeys.INDENT, "no");
        
        contentRetrieverFactory = ExistDBContentRetrieverFactory.getInstance(this);

    }

    @Override
    public XmlObject getContent(String contentId) {
        if (contentId == null)
            return null;
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, IEntity<?>> persist(
            java.util.Collection<? extends IEntity<?>> entities) {
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
                sbNS.append("<"+WRAPPER_ELEMENT);
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
                    + "</"+WRAPPER_ELEMENT+">");
                col.storeResource(res);
                resId = res.getId();
                xc.removeXml();
                xc.beginElement("xinclude", "gov:nist:scap:content-repo");
                xc.insertAttributeWithValue("resource-id", resId);
                resultResult.put(resId, ie);
            } catch (XMLDBException e) {
            	// TODO: log exception
                e.printStackTrace();
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

        return resultResult;

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

    private static Collection getOrCreateCollection(String collectionUri)
            throws XMLDBException {
        return getOrCreateCollection(collectionUri, 0);
    }

    private static Collection getOrCreateCollection(
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

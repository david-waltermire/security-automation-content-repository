package gov.nist.scap.content.exist;

import gov.nist.scap.content.shredder.rules.xmlbeans.TestContentHandler;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.sql.DriverManager;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.transform.OutputKeys;

import org.exist.xmldb.EXistResource;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.query.EntityStatistic;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.CollectionManagementService;
import org.xmldb.api.modules.XMLResource;

public class ExistDBContentPersistenceManager implements
        ContentPersistenceManager {

    private static final String URI =
        "xmldb:exist://localhost:8080/exist/xmlrpc/db/";
    private static final String COLLECTION = "dsig";
    private static final String USERNAME = "admin";
    private static final String PASSWORD = "";

    private Collection col = null;

    public ExistDBContentPersistenceManager()
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

        try {

            Class.forName("org.gjt.mm.mysql.Driver"); // Load the driver

            Connection conn =
                DriverManager.getConnection(
                    "jdbc:mysql://localhost/data",
                    "root",
                    ""); // Connect

        }

        catch (Exception err) {
        }

    }

    public Entity getEntityByKey(Key key, MetadataModel model) {
        if (key == null)
            return null;
        OutputStream os =
            new BufferedOutputStream(new FileOutputStream("test-out.xml"));
        os.write(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n").getBytes());
        XMLResource res = null;

        try {
            res = (XMLResource)col.getResource(key.getId());
            res.getContentAsSAX(new TestContentHandler(
                col,
                namespaces,
                res.getId(),
                os));
            os.flush();
            os.close();
        } finally {
            try {
                ((EXistResource)res).freeResources();
            } catch (XMLDBException xe) {
                xe.printStackTrace();
            }

        }

    };

    @Override
    public Map<String, ? extends EntityStatistic> getEntityStatistics(
            Set<String> entityInfoIds,
            MetadataModel model) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Set<Key> getKeysForIndirectIds(
            String indirectType,
            java.util.Collection<String> indirectIds,
            Set<String> entityType) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void storeEntities(
            List<? extends Entity> entities,
            MetadataModel model) throws ContentException {
        // TODO Auto-generated method stub

    }

    @Override
    public void shutdown() {
        if (col != null) {
            try {
                col.close();
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

                Collection start = DatabaseManager.getCollection(URI + path);
                if (start == null) {
                    // collection does not exist, so create
                    String parentPath =
                        path.substring(0, path.lastIndexOf("/"));
                    Collection parent =
                        DatabaseManager.getCollection(URI + parentPath);
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
        } else {
            return col;
        }
    }

}

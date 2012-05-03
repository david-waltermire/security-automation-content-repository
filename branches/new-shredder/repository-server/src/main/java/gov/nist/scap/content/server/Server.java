package gov.nist.scap.content.server;

import static org.scapdev.content.core.query.Conditional.allOf;
import static org.scapdev.content.core.query.Conditional.anyOf;
import static org.scapdev.content.core.query.entity.ContentId.contentId;
import static org.scapdev.content.core.query.entity.EntityContext.entityType;
import static org.scapdev.content.core.query.entity.EntityQuery.selectEntitiesWith;
import static org.scapdev.content.core.query.entity.Key.field;
import static org.scapdev.content.core.query.entity.Key.key;
import static org.scapdev.content.core.query.relationship.Relationship.relationship;
import static org.scapdev.content.core.query.relationship.RelationshipContext.relationshipType;
import static org.scapdev.content.core.query.relationship.To.to;
import static org.scapdev.content.core.query.relationship.ToBoundaryIdentifier.toBoundaryIdentifier;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.semantic.TripleStoreFacadeMetaDataManager;
import gov.nist.scap.content.shredder.parser.ContentHandler;
import gov.nist.scap.content.shredder.parser.ContentShredder;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.List;
import java.util.SortedMap;

import org.apache.xmlbeans.XmlException;
import org.openrdf.repository.RepositoryException;
import org.restlet.Component;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.InputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.scapdev.content.core.query.entity.EntityQuery;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Server extends ServerResource {

    public static String CONTENT_STORE_CLASS = "";
    public static String METADATA_STORE_CLASS = "";

    private ContentPersistenceManager contentRepo;
    private ContentShredder shredder;

    private static ApplicationContext context =
        new ClassPathXmlApplicationContext(new String[] {
            "spring-beans.xml"
        });

    public Server() {
        contentRepo =
            (ContentPersistenceManager)context.getBean("defaultLocalRepository");
        shredder = (ContentShredder)context.getBean("defaultContentShredder");
    }

    public static void main(String[] args) throws Exception {
        Runtime.getRuntime().addShutdownHook(
            new ShutdownHook(
                (ContentPersistenceManager)context.getBean("defaultLocalRepository")));
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8080);
        component.getDefaultHost().attach("/content-repo", Server.class);
        component.start();

        System.in.read();
        System.exit(0);

    }

    @Override
    protected Representation post(Representation entity)
            throws ResourceException {

        if ("submit".equals(getQuery().getFirstValue("type"))) {
            try {
                ContentHandler handler =
                    (ContentHandler)context.getBean("defaultContentHandler");
                BufferedWriter bw =
                    new BufferedWriter(new FileWriter("charout.txt"));
                InputStream is =
                    new PassThroughInputStream(entity.getStream(), bw);
                shredder.shred(is, handler);
                List<String> storedEntities =
                    contentRepo.storeEntities(handler.getEntities());
                setStatus(Status.SUCCESS_OK);
                return new InputRepresentation(
                    new ByteArrayInputStream(
                        ("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<content-id>"
                            + storedEntities.get(storedEntities.size() - 1) + "</content-id>").getBytes()),
                    MediaType.TEXT_XML);

            } catch (XmlException e) {
                throw new ResourceException(e);
            } catch (IOException e) {
                throw new ResourceException(e);
            } catch (ProcessingException e) {
                throw new ResourceException(e);
            } catch (ContentException e) {
                throw new ResourceException(e);
            }
        } else if( "sparql-query".equals(getQuery().getFirstValue("type")) ) {
            // TODO delete this once testing is complete
            try {
                TripleStoreFacadeMetaDataManager tsfmdm = (TripleStoreFacadeMetaDataManager)context.getBean("defaultLocalMetadataStore");
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(entity.getStream()));
                StringBuilder sb = new StringBuilder();
                while( (line = br.readLine()) != null ) {
                    sb.append(line + "\n");
                }
                List<SortedMap<String,String>> r = tsfmdm.runSPARQL(sb.toString());

                StringBuilder returnString = new StringBuilder();
                returnString.append("<results>\n");

                for( SortedMap<String,String> m : r ) {
                    returnString.append("  <result>\n");
                    for( String key : m.keySet() ) {
                        returnString.append("    <key>"+key+"</key><value>"+m.get(key)+"</value>\n");
                    }
                    returnString.append("  </result>\n");
                }

                returnString.append("</result>\n");
                setStatus(Status.SUCCESS_OK);
                return new InputRepresentation(
                    new ByteArrayInputStream(
                        (returnString.toString()).getBytes()),
                    MediaType.TEXT_XML);

            } catch (IOException e) {
                e.printStackTrace();
            } catch (RepositoryException e) {
                e.printStackTrace();
            }
        } else if ("test".equals(getQuery().getFirstValue("type"))) {
            // TODO delete this once testing is complete
            TripleStoreFacadeMetaDataManager tsfmdm = (TripleStoreFacadeMetaDataManager)context.getBean("defaultLocalMetadataStore");

    		EntityQuery query = selectEntitiesWith(
    				allOf(
//		    				anyOf(
//		    						entityType("http://oval.mitre.org/resource/content/definition/5#content-node-test"),
//		    						entityType("http://oval.mitre.org/resource/content/definition/5#content-node-definition")
//		    				),
		    				relationship(
//		    						anyOf(
//		    								relationshipType("http://scap.nist.gov/resource/content/model#hasParentRelationshipTo"),
		    								toBoundaryIdentifier("http://cce.mitre.org/resource/content/cce#external-identifier-cce-5", "CCE-13091-4")
//		    						)
		    				)
	    			)
    		);
    		Collection<? extends IEntity<?>> results;
			try {
				results = tsfmdm.getEntities(query);
	            StringBuilder returnString = new StringBuilder();
	            returnString.append("<results>\n");

	            for (IEntity<?> entityObj : results) {
	                returnString.append("  <class>");
	                returnString.append(entityObj.getDefinition().getId());
	                returnString.append("</class>\n");
//	                    returnString.append("  <uri>");
//	                    returnString.append(entityObj.getUri());
//	                    returnString.append("</uri>\n");
	            }

	            returnString.append("</result>\n");
	            setStatus(Status.SUCCESS_OK);
	            return new InputRepresentation(
	                new ByteArrayInputStream(
	                    (returnString.toString()).getBytes()),
	                MediaType.TEXT_XML);
			} catch (ProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }

        setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        return null;
    }

    @Override
    protected Representation get() throws ResourceException {
        try {
            IEntity<?> entity =
                contentRepo.getEntity(getQuery().getFirstValue("content-id"));
            if (entity != null) {
                setStatus(Status.SUCCESS_OK);
                return new InputRepresentation(
                    entity.getContentHandle().getCursor().getObject().newInputStream(),
                    MediaType.TEXT_XML);
            } else {
                setStatus(Status.SUCCESS_NO_CONTENT);
                return null;
            }
        } catch (ProcessingException e) {
            throw new ResourceException(e);
        }
    }

    private static class ShutdownHook extends Thread {
        private ContentPersistenceManager cpm;

        public ShutdownHook(ContentPersistenceManager cpm) {
            this.cpm = cpm;
        }

        public void run() {
            cpm.shutdown();
        }
    }
}

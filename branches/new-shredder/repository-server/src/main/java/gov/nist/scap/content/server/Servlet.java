package gov.nist.scap.content.server;

import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.shredder.parser.ContentHandler;
import gov.nist.scap.content.shredder.parser.ContentShredder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.xmlbeans.XmlException;
import org.restlet.Component;
import org.restlet.data.Protocol;
import org.restlet.data.Status;
import org.restlet.representation.EmptyRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;
import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.ContentPersistenceManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Servlet extends ServerResource {

    public static String CONTENT_STORE_CLASS = "";
    public static String METADATA_STORE_CLASS = "";

    private ContentPersistenceManager contentRepo;
    private ContentShredder shredder;

    private static ApplicationContext context = new ClassPathXmlApplicationContext(
        new String[] {"spring-beans.xml"});

    
    public Servlet() {
        contentRepo = (ContentPersistenceManager)context.getBean("defaultLocalRepository");
        shredder = (ContentShredder)context.getBean("defaultContentShredder");
    }

    public static void main(String[] args) throws Exception {
        Component component = new Component();  
        component.getServers().add(Protocol.HTTP, 8080);  
        component.getDefaultHost().attach("/content-repo", Servlet.class);  
        component.start();  
    }
    
    @Override
    protected Representation post(Representation entity)
            throws ResourceException {
        
        try {
            ContentHandler handler = (ContentHandler)context.getBean("defaultContentHandler");
            BufferedWriter bw = new BufferedWriter(new FileWriter("charout.txt"));
            InputStream is = new PassThroughInputStream(entity.getStream(), bw);
            shredder.shred(
                is,
                handler);
            contentRepo.storeEntities(handler.getEntities());
            setStatus(Status.SUCCESS_ACCEPTED);
            return new EmptyRepresentation();
            
        } catch (XmlException e) {
            throw new ResourceException(e);
        } catch (IOException e) {
            throw new ResourceException(e);
        } catch (ProcessingException e) {
            throw new ResourceException(e);
        } catch (ContentException e) {
            throw new ResourceException(e);
        }
    }
    
}

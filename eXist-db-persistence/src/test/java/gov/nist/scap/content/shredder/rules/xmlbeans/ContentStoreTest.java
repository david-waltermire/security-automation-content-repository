package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.exist.ExistDBContentStore;
import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.scapdev.content.core.persistence.hybrid.ContentStore;
import org.xmldb.api.base.XMLDBException;

public class ContentStoreTest {

    @Test
    public void testContentStore()
            throws ClassNotFoundException, XMLDBException,
            InstantiationException, IllegalAccessException, XmlException, IOException, ContentException, ProcessingException {
        XmlbeansRules xmlbeansRules = new XmlbeansRules(new File("test.xml"));

        RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
        ContentShredder shredder = new ContentShredder(rules);
        DataExtractingContentHandler handler =
            new DataExtractingContentHandler();
        shredder.shred(new File("scap_gov.nist_USGCB-Windows-7.xml"), handler);
        Collection<? extends IEntity<?>> entities = handler.getEntities();

        ContentStore cs = new ExistDBContentStore();
        Map<String, IEntity<?>> result = cs.persist(entities);
        IEntity<?> ie = null;
        for( IEntity<?> iee : entities ) {
            ie = iee;
        }
        for( String s : result.keySet() ) {
            if( result.get(s) == ie ) {
                XmlCursor xml = cs.getContentRetriever(s).getCursor();
                FileOutputStream fos = new FileOutputStream("out.xml");
                fos.write(xml.xmlText().getBytes("UTF-8"));
                fos.flush();
                fos.close();
            }
        }
        
    }
}

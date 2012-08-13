package org.scapdev.content.core.query;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.server.EntityCollectionStreamingOutput;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;
import gov.nist.scap.content.shredder.rules.xmlbeans.XmlbeansRules;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;

public class EntityTransportTest {

	@SuppressWarnings("static-method")
	@Test
	public void test() throws IOException, XmlException, ProcessingException {
		XmlbeansRules xmlbeansRules =
                new XmlbeansRules(this.getClass().getResourceAsStream(
                    "/rules.xml"));

            String testFile = "scap-data-stream-multi-signatures.xml";

            RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
            ContentShredder shredder = new ContentShredder(rules);
            DataExtractingContentHandler handler =
                new DataExtractingContentHandler();
            shredder.shred(
            		this.getClass().getResourceAsStream("/" + testFile),
                handler);
            List<? extends IEntity<?>> entities = handler.getEntities();
            
            
            EntityCollectionStreamingOutput ecso = new EntityCollectionStreamingOutput(Collections.singletonList(entities.get(entities.size()-1)));
            //EntityCollectionStreamingOutput ecso = new EntityCollectionStreamingOutput(entities);
            OutputStream os = new BufferedOutputStream(new FileOutputStream("entities-out.xml"));
            ecso.write(os);
            os.flush();
            os.close();
	}

}

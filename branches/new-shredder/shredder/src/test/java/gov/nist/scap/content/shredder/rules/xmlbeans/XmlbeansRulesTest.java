package gov.nist.scap.content.shredder.rules.xmlbeans;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.RuleDefinitions;
import gov.nist.scap.content.shredder.parser.ContentShredder;
import gov.nist.scap.content.shredder.parser.DataExtractingContentHandler;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.junit.Test;

public class XmlbeansRulesTest {

	@Test
	public void testXmlbeansRulesFile() throws XmlException, IOException, ContentException, ProcessingException {
		XmlbeansRules xmlbeansRules = new XmlbeansRules(new File("test.xml"));

		RuleDefinitions rules = xmlbeansRules.getRuleDefinitions();
		ContentShredder shredder = new ContentShredder(rules);
		DataExtractingContentHandler handler = new DataExtractingContentHandler();
//		shredder.shred(new File("C:\\code\\scap\\USGCB-1.2.3.1\\scap_gov.nist_USGCB-Windows-XP-firewall.xml"));
		shredder.shred(new File("scap_gov.nist_USGCB-Windows-7.xml"), handler);
		Collection<? extends IEntity<?>> entities = handler.getEntities();
		for (IEntity<?> entity : entities) {
			XmlCursor cursor = entity.getContentHandle().getCursor();
			QName qname = cursor.getName();
			System.out.println("Entity("+entity.getRelationships().size()+"): "+qname.toString());
		}
	}

}

package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.rules.IDocumentDefinition;
import gov.nist.scap.content.shredder.rules.ProcessingException;
import gov.nist.scap.content.shredder.rules.RuleDefinitions;

import java.io.File;
import java.io.IOException;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;

public class ContentShredder {
	private static final Logger log = Logger.getLogger(ContentShredder.class);
	private final RuleDefinitions ruleDefinitions;

	public ContentShredder(RuleDefinitions ruleDefinitions) {
		this.ruleDefinitions = ruleDefinitions;
	}

	public void shred(File file, ContentHandler handler) throws XmlException, IOException, ContentException, ProcessingException {
		shred(XmlObject.Factory.parse(file), handler);
	}

	public void shred(XmlObject obj, ContentHandler handler) throws ContentException, ProcessingException {
		XmlCursor cursor = obj.newCursor();

		if (cursor.isStartdoc()) {
			if (!cursor.toFirstChild()) {
				throw new ContentException("document has no child");
			}
		}

		shred(cursor, handler);
		cursor.dispose();
	}

	public void shred(XmlCursor cursor, ContentHandler handler) throws ContentException, ProcessingException {
		QName name = cursor.getName();
		log.debug("Shredding element with qname: "+name);

		IDocumentDefinition documentDef = ruleDefinitions.getDocumentDefinition(name);
		if (documentDef != null) {
			documentDef.processCursor(cursor, handler, null);
		}
	}
}

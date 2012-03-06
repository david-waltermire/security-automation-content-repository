package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.definitions.IDocumentDefinition;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.RuleDefinitions;

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

	public void shred(File file, ContentHandler contentHandler) throws XmlException, IOException, ContentException, ProcessingException {
		shred(XmlObject.Factory.parse(file), contentHandler);
	}

	public void shred(XmlObject obj, ContentHandler contentHandler) throws ContentException, ProcessingException {
		XmlCursor cursor = obj.newCursor();

		if (cursor.isStartdoc()) {
			if (!cursor.toFirstChild()) {
				throw new ContentException("document has no child");
			}
		}

		shred(cursor, contentHandler);
		cursor.dispose();
	}

	public void shred(XmlCursor cursor, ContentHandler contentHandler) throws ContentException, ProcessingException {
		QName name = cursor.getName();
		log.debug("Shredding element with qname: "+name);

		ContentProcessor processor = new ContentProcessor(contentHandler);
		IDocumentDefinition documentDef = ruleDefinitions.getDocumentDefinition(name);
		if (documentDef != null) {
			processor.process(documentDef, cursor);
		} else {
			throw new ContentException("Unsupported document type: "+name.toString());
		}
	}
}

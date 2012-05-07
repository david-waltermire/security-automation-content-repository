package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.definitions.IDocumentDefinition;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.RuleDefinitions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContentShredder {
	private static final Logger log = LoggerFactory.getLogger(ContentShredder.class);
	private final RuleDefinitions ruleDefinitions;

	public ContentShredder(RuleDefinitions ruleDefinitions) {
		this.ruleDefinitions = ruleDefinitions;
	}

	public void shred(File file, ContentHandler contentHandler) throws XmlException, IOException, ProcessingException {
		shred(XmlObject.Factory.parse(file), contentHandler);
	}

    public void shred(InputStream is, ContentHandler contentHandler) throws XmlException, IOException, ProcessingException {
        shred(XmlObject.Factory.parse(is), contentHandler);
    }
	
	public void shred(XmlObject obj, ContentHandler contentHandler) throws ProcessingException {
		XmlCursor cursor = obj.newCursor();

		if (cursor.isStartdoc()) {
			if (!cursor.toFirstChild()) {
				throw new ProcessingException("document has no child");
			}
		}

		shred(cursor, contentHandler);
		cursor.dispose();
	}

	public void shred(XmlCursor cursor, ContentHandler contentHandler) throws ProcessingException {
		QName name = cursor.getName();
		log.debug("Shredding element with qname: "+name);

		ContentProcessor processor = new ContentProcessor(contentHandler);
		IDocumentDefinition documentDef = ruleDefinitions.getDocumentDefinition(name);
		if (documentDef != null) {
			processor.process(documentDef, cursor);
		} else {
			throw new ProcessingException("Unsupported document type: "+name.toString());
		}
	}
}

package gov.nist.scap.content.shredder.model;


import gov.nist.scap.content.shredder.rules.IDocumentDefinition;

import org.apache.xmlbeans.XmlCursor;


public abstract class AbstractDocument<DEFINITION extends IDocumentDefinition> extends AbstractEntity<DEFINITION> implements IDocument<DEFINITION> {

	public AbstractDocument(XmlCursor cursor,
			DEFINITION documentDefinition,
			IEntity<?> parentContext) throws ContentException {
		super(cursor, documentDefinition, parentContext);
	}
}

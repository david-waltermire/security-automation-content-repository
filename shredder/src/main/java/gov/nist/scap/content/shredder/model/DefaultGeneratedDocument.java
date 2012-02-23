package gov.nist.scap.content.shredder.model;


import gov.nist.scap.content.shredder.rules.IGeneratedDocumentDefinition;

import org.apache.xmlbeans.XmlCursor;

public class DefaultGeneratedDocument extends AbstractDocument<IGeneratedDocumentDefinition> implements IGeneratedDocument {

	public DefaultGeneratedDocument(XmlCursor cursor,
			IGeneratedDocumentDefinition documentDefinition,
			IContainer<?> parentContext) throws ContentException {
		super(cursor, documentDefinition, parentContext);
	}

	public IKey getKey() {
		return null;
	}

	public void accept(IContainerVisitor visitor) {
		visitor.visit(this);
	}
}

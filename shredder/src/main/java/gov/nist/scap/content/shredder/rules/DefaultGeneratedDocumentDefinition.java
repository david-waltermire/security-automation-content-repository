package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.DefaultGeneratedDocument;
import gov.nist.scap.content.shredder.model.IContainer;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;

public class DefaultGeneratedDocumentDefinition extends AbstractDocumentDefinition implements IGeneratedDocumentDefinition {

	public DefaultGeneratedDocumentDefinition(ISchema schema, String id, QName name) {
		super(schema, id, name);
	}

	@Override
	protected DefaultGeneratedDocument newContainer(XmlCursor cursor,
			IContainer<?> parentContext) throws ContentException {
		return new DefaultGeneratedDocument(cursor, this, parentContext);
	}
}

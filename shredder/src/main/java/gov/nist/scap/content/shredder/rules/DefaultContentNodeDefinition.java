package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.DefaultContentNode;
import gov.nist.scap.content.shredder.model.IEntity;
import gov.nist.scap.content.shredder.model.IKey;

import org.apache.xmlbeans.XmlCursor;


public class DefaultContentNodeDefinition extends AbstractContentNodeDefinition  {

	public DefaultContentNodeDefinition(ISchema schema, String id, IKeyDefinition keyDefinition) {
		super(schema, id, keyDefinition);
	}

	@Override
	protected DefaultContentNode newEntity(XmlCursor cursor, IEntity<?> parentContext,
			IKey key) throws ContentException {
		return new DefaultContentNode(cursor, this, parentContext, key);
	}
}

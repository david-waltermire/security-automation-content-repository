package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.parser.ContentHandler;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class DefaultKeyedRelationshipDefinition extends AbstractRelationshipDefinition implements
		IKeyedRelationshipDefinition {
	private final IKeyRefDefinition keyRefDefinition;

	public DefaultKeyedRelationshipDefinition(ISchema schema, String id, String xpath, IKeyRefDefinition keyRefDefinition) throws XmlException {
		super(schema, id, xpath);

		if (keyRefDefinition == null) {
			throw new NullPointerException("keyRefDefinition");
		}

		this.keyRefDefinition = keyRefDefinition;
	}

	public IKeyRefDefinition getKeyRefDefinition() {
		return keyRefDefinition;
	}

	@Override
	protected void handleRelationship(XmlCursor cursor,
			ContentHandler handler, IMutableEntity<?> containingEntity)
			throws ProcessingException, ContentException {
		IKey key = getKeyRefDefinition().getKey(containingEntity, cursor);

		handler.handle(new KeyedRelationshipInfo(this, containingEntity, key));
	}
}

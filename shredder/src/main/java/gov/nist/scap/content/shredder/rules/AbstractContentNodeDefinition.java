package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.IMutableContentNode;

import org.apache.xmlbeans.XmlCursor;

public abstract class AbstractContentNodeDefinition extends AbstractEntityDefinition implements IContentNodeDefinition {

	private final IKeyDefinition keyDefinition;

	public AbstractContentNodeDefinition(ISchema schema, String id, IKeyDefinition keyDefinition) {
		super(schema, id);
		this.keyDefinition = keyDefinition;
	}

	protected abstract IMutableContentNode newEntity(XmlCursor cursor, IContainer<?> parentContext,
			IKey key) throws ContentException;

	@Override
	protected final IMutableContentNode newContainer(XmlCursor cursor,
			IContainer<?> parentContext) throws ContentException {
		IKey key = keyDefinition.getKey(parentContext, cursor);
		return newEntity(cursor, parentContext, key);
	}
}

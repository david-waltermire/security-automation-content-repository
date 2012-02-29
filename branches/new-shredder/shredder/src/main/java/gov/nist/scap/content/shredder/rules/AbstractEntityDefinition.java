package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.parser.ContentHandler;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.xmlbeans.XmlCursor;

public abstract class AbstractEntityDefinition extends AbstractDefinition implements IEntityDefinition {
	private final Collection<IRelationshipDefinition> relationships = new LinkedList<IRelationshipDefinition>();

	public AbstractEntityDefinition(ISchema schema, String id) {
		super(schema, id);
	}

	protected abstract IMutableEntity<?> newContainer(XmlCursor cursor, IContainer<?> parentContext) throws ContentException;

	public void addRelationship(IRelationshipDefinition relationship) {
		relationships.add(relationship);
	}

	public IMutableEntity<?> processCursor(XmlCursor cursor, ContentHandler handler, IMutableEntity<?> parent) throws ProcessingException, ContentException {
		IMutableEntity<?> entity = newContainer(cursor, parent);

		// Handle all relationships
		for (IRelationshipDefinition relationship : relationships) {
			relationship.processCursor(cursor, handler, entity);
		}

		handler.handle(entity);
		return entity;
	}
}

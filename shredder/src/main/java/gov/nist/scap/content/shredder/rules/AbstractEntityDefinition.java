package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.DefaultBoundaryRelationship;
import gov.nist.scap.content.shredder.model.IBoundaryRelationship;
import gov.nist.scap.content.shredder.model.IEntity;
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

	protected abstract IMutableEntity<?> newContainer(XmlCursor cursor, IEntity<?> parentContext) throws ContentException;

	public void addRelationship(IRelationshipDefinition relationship) {
		relationships.add(relationship);
	}

	public void processCursor(XmlCursor cursor, IBoundaryRelationshipDefinition boundaryDef, ContentHandler handler, IMutableEntity<?> parent) throws ProcessingException, ContentException {
		IMutableEntity<?> entity = newContainer(cursor, parent);

		if (boundaryDef != null) {
			IBoundaryRelationship relationship = new DefaultBoundaryRelationship(boundaryDef, entity, parent);
			entity.appendRelationship(relationship);
		}

		// Handle all relationships
		for (IRelationshipDefinition relationship : relationships) {
			relationship.processCursor(cursor, handler, entity);
		}

		handler.handle(entity);
	}
}

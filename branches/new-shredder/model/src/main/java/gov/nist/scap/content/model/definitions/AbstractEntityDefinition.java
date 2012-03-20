package gov.nist.scap.content.model.definitions;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public abstract class AbstractEntityDefinition extends AbstractSchemaRelatedDefinition implements IEntityDefinition {
	private final Collection<IRelationshipDefinition> relationships = new LinkedList<IRelationshipDefinition>();
	private final Collection<IPropertyRef> propertyRefs = new LinkedList<IPropertyRef>();

	public AbstractEntityDefinition(ISchemaDefinition schema, String id) {
		super(schema, id);
	}

	public void addRelationship(IRelationshipDefinition relationship) {
		relationships.add(relationship);
	}

	public Collection<IRelationshipDefinition> getRelationshipDefinitions() {
		return Collections.unmodifiableCollection(relationships);
	}

	public void addPropertyRefDefinition(IPropertyRef propertyRef) {
		propertyRefs.add(propertyRef);
	}

	public Collection<IPropertyRef> getPropertyRefs() {
		return Collections.unmodifiableCollection(propertyRefs);
	}

}

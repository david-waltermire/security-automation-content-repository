package gov.nist.scap.content.shredder.rules;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

public abstract class AbstractEntityDefinition extends AbstractDefinition implements IEntityDefinition {
	private final Collection<IRelationshipDefinition> relationships = new LinkedList<IRelationshipDefinition>();

	public AbstractEntityDefinition(ISchema schema, String id) {
		super(schema, id);
	}

	public void addRelationship(IRelationshipDefinition relationship) {
		relationships.add(relationship);
	}

	public Collection<IRelationshipDefinition> getRelationshipDefinitions() {
		return Collections.unmodifiableCollection(relationships);
	}
}

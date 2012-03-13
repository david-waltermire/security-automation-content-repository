package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;

public class DefaultCompositeRelationship extends AbstractRelationship<ICompositeRelationshipDefinition> implements ICompositeRelationship {
	private final IEntity<?> containingObject;

	public DefaultCompositeRelationship(
			ICompositeRelationshipDefinition definition,
			IEntity<?> owningEntity,
			IEntity<?> containingObject) {
		super(definition, owningEntity);
		this.containingObject = containingObject;
	}

	public IEntity<?> getContainingObject() {
		return containingObject;
	}

	public void accept(IRelationshipVisitor visitor) {
		visitor.visit(this);
	}
}

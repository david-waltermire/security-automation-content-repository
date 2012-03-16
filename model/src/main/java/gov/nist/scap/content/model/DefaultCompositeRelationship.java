package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;

public class DefaultCompositeRelationship extends AbstractRelationship<ICompositeRelationshipDefinition> implements ICompositeRelationship {
	private final IEntity<?> childEntity;

	public DefaultCompositeRelationship(
			ICompositeRelationshipDefinition definition,
			IEntity<?> parentEntity,
			IEntity<?> childEntity) {
		super(definition, parentEntity);
		this.childEntity = childEntity;
	}

	public IEntity<?> getChildEntity() {
		return childEntity;
	}

	public void accept(IRelationshipVisitor visitor) {
		visitor.visit(this);
	}
}

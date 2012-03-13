package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;

public class DefaultBoundaryIdentifierRelationship extends AbstractRelationship<IBoundaryIdentifierRelationshipDefinition> implements IBoundaryIdentifierRelationship {
	private final IExternalIdentifier externalIdentifier;
	private final String value;

	public DefaultBoundaryIdentifierRelationship(
			IBoundaryIdentifierRelationshipDefinition definition, IEntity<?> owningEntity, IExternalIdentifier externalIdentifier, String value) {
		super(definition, owningEntity);
		this.externalIdentifier = externalIdentifier;
		this.value = value;
	}

	/**
	 * @return the externalIdentifier
	 */
	public IExternalIdentifier getExternalIdentifier() {
		return externalIdentifier;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	public void accept(IRelationshipVisitor visitor) {
		visitor.visit(this);
	}

}

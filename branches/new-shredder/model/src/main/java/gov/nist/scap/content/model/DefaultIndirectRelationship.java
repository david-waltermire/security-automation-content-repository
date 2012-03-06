package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IIndirectRelationshipDefinition;

public class DefaultIndirectRelationship extends AbstractRelationship<IIndirectRelationshipDefinition> implements IIndirectRelationship {
	private final IExternalIdentifier externalIdentifier;
	private final String value;

	public DefaultIndirectRelationship(
			IIndirectRelationshipDefinition definition, IEntity<?> owningEntity, IExternalIdentifier externalIdentifier, String value) {
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

}

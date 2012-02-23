package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IExternalIdentifier;
import gov.nist.scap.content.shredder.rules.IIndirectRelationshipDefinition;

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

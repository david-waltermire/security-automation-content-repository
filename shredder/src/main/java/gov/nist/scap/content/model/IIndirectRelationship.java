package gov.nist.scap.content.model;

import gov.nist.scap.content.definitions.IExternalIdentifier;
import gov.nist.scap.content.definitions.IIndirectRelationshipDefinition;

public interface IIndirectRelationship extends IRelationship<IIndirectRelationshipDefinition> {
	IExternalIdentifier getExternalIdentifier();
	String getValue();
}

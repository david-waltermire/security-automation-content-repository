package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IIndirectRelationshipDefinition;

public interface IIndirectRelationship extends IRelationship<IIndirectRelationshipDefinition> {
	IExternalIdentifier getExternalIdentifier();
	String getValue();
}

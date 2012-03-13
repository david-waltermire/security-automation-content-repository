package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;

public interface IBoundaryIdentifierRelationship extends IRelationship<IBoundaryIdentifierRelationshipDefinition> {
	IExternalIdentifier getExternalIdentifier();
	String getValue();
}

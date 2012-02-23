package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IExternalIdentifier;
import gov.nist.scap.content.shredder.rules.IIndirectRelationshipDefinition;

public interface IIndirectRelationship extends IRelationship<IIndirectRelationshipDefinition> {
	IExternalIdentifier getExternalIdentifier();
	String getValue();
}

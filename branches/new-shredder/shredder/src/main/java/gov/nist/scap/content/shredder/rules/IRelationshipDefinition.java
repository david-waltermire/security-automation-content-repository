package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;


public interface IRelationshipDefinition extends IDefinition {
	void accept(IRelationshipDefinitionVisitor visitor) throws ContentException, ProcessingException;
	String getXpath();
}

package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;


public interface IRelationshipDefinition extends IDefinition {
	void accept(IRelationshipDefinitionVisitor visitor) throws ContentException, ProcessingException;
	String getXpath();
}

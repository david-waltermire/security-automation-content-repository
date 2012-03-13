package gov.nist.scap.content.model.definitions;



public interface IRelationshipDefinition extends IDefinition {
	void accept(IRelationshipDefinitionVisitor visitor) throws ProcessingException;
	String getXpath();
}

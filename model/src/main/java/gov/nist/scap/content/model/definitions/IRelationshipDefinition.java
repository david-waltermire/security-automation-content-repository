package gov.nist.scap.content.model.definitions;



public interface IRelationshipDefinition extends ISchemaRelatedDefinition {
	void accept(IRelationshipDefinitionVisitor visitor) throws ProcessingException;
	String getXpath();
}

package gov.nist.scap.content.model.definitions;


public interface IIndirectRelationshipDefinition extends IRelationshipDefinition {
	XPathRetriever getValueRetriever();
	IExternalIdentifierMapping getQualifierMapping();
}

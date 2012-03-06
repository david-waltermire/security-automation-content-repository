package gov.nist.scap.content.definitions;


public interface IIndirectRelationshipDefinition extends IRelationshipDefinition {
	XPathRetriever getValueRetriever();
	IExternalIdentifierMapping getQualifierMapping();
}

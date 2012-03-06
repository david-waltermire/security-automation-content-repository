package gov.nist.scap.content.definitions;

public interface IKeyedRelationshipDefinition extends IRelationshipDefinition {
	IKeyRefDefinition getKeyRefDefinition();
}

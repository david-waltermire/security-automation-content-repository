package gov.nist.scap.content.shredder.rules;

public interface IKeyedEntityDefinition extends IEntityDefinition {
	IKeyDefinition getKeyDefinition();
}

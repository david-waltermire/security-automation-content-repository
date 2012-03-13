package gov.nist.scap.content.model.definitions;




public class DefaultContentNodeDefinition extends AbstractContentNodeDefinition  {

	public DefaultContentNodeDefinition(ISchema schema, String id, IKeyDefinition keyDefinition) {
		super(schema, id, keyDefinition);
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ProcessingException {
		return visitor.visit(this);
	}
}

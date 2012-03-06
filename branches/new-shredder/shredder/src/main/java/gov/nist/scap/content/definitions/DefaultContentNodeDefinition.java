package gov.nist.scap.content.definitions;

import gov.nist.scap.content.model.ContentException;



public class DefaultContentNodeDefinition extends AbstractContentNodeDefinition  {

	public DefaultContentNodeDefinition(ISchema schema, String id, IKeyDefinition keyDefinition) {
		super(schema, id, keyDefinition);
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ContentException, ProcessingException {
		return visitor.visit(this);
	}
}

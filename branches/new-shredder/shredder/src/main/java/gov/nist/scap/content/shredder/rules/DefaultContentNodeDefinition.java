package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;



public class DefaultContentNodeDefinition extends AbstractContentNodeDefinition  {

	public DefaultContentNodeDefinition(ISchema schema, String id, IKeyDefinition keyDefinition) {
		super(schema, id, keyDefinition);
	}

	public <T> T accept(IEntityDefinitionVisitor<T> visitor) throws ContentException, ProcessingException {
		return visitor.visit(this);
	}
}

package gov.nist.scap.content.definitions;

import gov.nist.scap.content.model.ContentException;

import org.apache.xmlbeans.XmlException;

public class DefaultKeyedRelationshipDefinition extends AbstractRelationshipDefinition implements
		IKeyedRelationshipDefinition {
	private final IKeyRefDefinition keyRefDefinition;

	public DefaultKeyedRelationshipDefinition(ISchema schema, String id, String xpath, IKeyRefDefinition keyRefDefinition) throws XmlException {
		super(schema, id, xpath);

		if (keyRefDefinition == null) {
			throw new NullPointerException("keyRefDefinition");
		}

		this.keyRefDefinition = keyRefDefinition;
	}

	public IKeyRefDefinition getKeyRefDefinition() {
		return keyRefDefinition;
	}

	public void accept(IRelationshipDefinitionVisitor visitor) throws ContentException, ProcessingException {
		visitor.visit(this);
	}
}

package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;

import org.apache.xmlbeans.XmlCursor;

public class AbstractContentNode extends AbstractEntity<IContentNodeDefinition> implements IMutableContentNode {
	private final IKey key;

	public AbstractContentNode(XmlCursor cursor,
			IContentNodeDefinition contentNodeDefinition,
			IEntity<?> parentContext, IKey key) throws ContentException {
		super(cursor, contentNodeDefinition, parentContext);
		this.key = key;
	}

	public IKey getKey() {
		return key;
	}

	public IKey getKey(String keyId) {
		IKey retval;
		if (key.getId().equals(keyId)) {
			retval = key;
		} else {
			retval = getParentContext().getKey(keyId);
		}
		return retval;
	}

	public void accept(IContainerVisitor visitor) {
		visitor.visit(this);
	}
}

package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;

import org.apache.xmlbeans.XmlCursor;

public class DefaultContentNode extends AbstractContentNode {

	public DefaultContentNode(XmlCursor cursor, IContentNodeDefinition contentNodeDefinition,
			IEntity<?> parentContext, IKey key) throws ContentException {
		super(cursor, contentNodeDefinition, parentContext, key);
	}
}

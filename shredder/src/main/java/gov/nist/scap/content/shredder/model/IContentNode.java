package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;


public interface IContentNode extends IIndexed, IEntity<IContentNodeDefinition> {
	IContentNodeDefinition getDefinition();
}

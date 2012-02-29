package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;


public interface IContentNode extends IKeyedEntity<IContentNodeDefinition>, IContainer<IContentNodeDefinition> {
	IContentNodeDefinition getDefinition();
}

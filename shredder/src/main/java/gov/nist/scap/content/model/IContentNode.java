package gov.nist.scap.content.model;

import gov.nist.scap.content.definitions.IContentNodeDefinition;


public interface IContentNode extends IKeyedEntity<IContentNodeDefinition> {
	IContentNodeDefinition getDefinition();
}

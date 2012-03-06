package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IDefinition;

/**
 * This interface represents a distinct segment of XML that has some predefined
 * significance.
 * 
 * @param <DEFINITION> the content rule that identifies the segment of XML
 */
public interface IContentConstruct<DEFINITION extends IDefinition> {
	DEFINITION getDefinition();
}

package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IDocumentDefinition;


public interface IDocument<DEFINITION extends IDocumentDefinition> extends IEntity<DEFINITION> {
	DEFINITION getDefinition();
}

package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IDocumentDefinition;


public interface IDocument<DEFINITION extends IDocumentDefinition> extends IContainer<DEFINITION> {
	DEFINITION getDefinition();
}

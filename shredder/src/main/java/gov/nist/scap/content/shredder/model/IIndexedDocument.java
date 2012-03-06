package gov.nist.scap.content.shredder.model;

import gov.nist.scap.content.shredder.rules.IKeyedDocumentDefinition;


public interface IIndexedDocument extends IDocument<IKeyedDocumentDefinition>, IKeyedEntity<IKeyedDocumentDefinition> {
}

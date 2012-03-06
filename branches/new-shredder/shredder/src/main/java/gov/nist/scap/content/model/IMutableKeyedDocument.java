package gov.nist.scap.content.model;

import gov.nist.scap.content.shredder.model.IIndexedDocument;
import gov.nist.scap.content.shredder.rules.IKeyedDocumentDefinition;

public interface IMutableKeyedDocument extends IIndexedDocument, IMutableEntity<IKeyedDocumentDefinition> {

}

package gov.nist.scap.content.model;

import gov.nist.scap.content.shredder.model.IGeneratedDocument;
import gov.nist.scap.content.shredder.rules.IGeneratedDocumentDefinition;

public interface IMutableGeneratedDocument extends IGeneratedDocument, IMutableEntity<IGeneratedDocumentDefinition> {

}

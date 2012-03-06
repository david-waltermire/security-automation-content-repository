package gov.nist.scap.content.model;

import gov.nist.scap.content.shredder.model.IContentNode;
import gov.nist.scap.content.shredder.rules.IContentNodeDefinition;

public interface IMutableContentNode extends IContentNode, IMutableEntity<IContentNodeDefinition> {

}

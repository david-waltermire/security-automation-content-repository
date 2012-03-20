package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;

/**
 * This interface represents a distinct segment of XML that identifies a
 * relationship between an entity and some other concept (often an entity) in
 * the underlying XML infoset. A relationship definition is directly
 * associated with the entity using the
 * {@link IEntityDefinition#addRelationship(IRelationshipDefinition)}
 * method.
 * 
 * @param <DEFINITION> the content rule that defines the relationship
 * @see IEntity
 * @see IEntityDefinition
 */
public interface IRelationship<DEFINITION extends IRelationshipDefinition> extends IContentConstruct<DEFINITION> {
	void accept(IRelationshipVisitor visitor);
}

package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IEntityDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This interface represents a distinct segment of XML that represents a concept
 * in the underlying XML model. This is the core interface for this model.
 * 
 * @param <DEFINITION> the content rule that identifies the entity, its relative
 * 		location to other entities and what relationships and properties the
 * 		entity may contain
 */
public interface IEntity<DEFINITION extends IEntityDefinition> extends IContentConstruct<DEFINITION> {
	/**
	 * Retrieves the parent entity, the entity containing the this entity in the
	 * XML infoset, if one exists.
	 * @return the parent entity or <code>null</code> if none exists
	 */
	IEntity<?> getParent();
	/**
	 * Retrieves the relationships associated with this entity.
	 * @return a non-null collection
	 */
	Collection<? extends IRelationship<?>> getRelationships();
	/**
	 * Retrieves the keyed relationships associated with this entity.
	 * @return a non-null collection
	 */
	Collection<? extends IKeyedRelationship> getKeyedRelationships();
	/**
	 * Retrieves the indirect relationships associated with this entity.
	 * @return a non-null collection
	 */
	Collection<? extends IBoundaryIdentifierRelationship> getBoundaryIdentifierRelationships();
	/**
	 * Retrieves a ContentHandle that can be used to access the underlying XML
	 * segment represented by the entity. 
	 * @return the ContentHandle associated with the entity
	 * @see IContentHandle
	 */
	IContentHandle getContentHandle();
	void accept(IEntityVisitor visitor);
	IVersion getVersion();
	Map<String, ? extends Set<String>> getProperties();
	List<String> getPropertyById(String id);
	
}

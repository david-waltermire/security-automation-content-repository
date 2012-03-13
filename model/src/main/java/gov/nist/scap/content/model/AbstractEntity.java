package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IEntityDefinition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;


public abstract class AbstractEntity<DEFINITION extends IEntityDefinition> implements IMutableEntity<DEFINITION> {
	private final DEFINITION contentDefinition;
	private final List<IRelationship<?>> relationships = new LinkedList<IRelationship<?>>();
	private final List<IKeyedRelationship> keyedRelationships = new LinkedList<IKeyedRelationship>();
	private final List<IBoundaryIdentifierRelationship> indirectRelationships = new LinkedList<IBoundaryIdentifierRelationship>();
	private final IContentHandle contentHandle;
	private final IMutableEntity<?> parent;

	public AbstractEntity(DEFINITION definition, IContentHandle contentHandle, IMutableEntity<?> parent) {
		this.contentDefinition = definition;
		this.contentHandle = contentHandle;
		this.parent = parent;
	}

	public DEFINITION getDefinition() {
		return contentDefinition;
	}

	public List<IRelationship<?>> getRelationships() {
		return Collections.unmodifiableList(relationships);
	}

	public List<IKeyedRelationship> getKeyedRelationships() {
		return Collections.unmodifiableList(keyedRelationships);
	}

	public List<IBoundaryIdentifierRelationship> getIndirectRelationships() {
		return Collections.unmodifiableList(indirectRelationships);
	}

	public IKey getKey(String keyId) {
		return (getParent() == null ? null : getParent().getKey(keyId));
	}

	public IMutableEntity<?> getParent() {
		return parent;
	}

	public IContentHandle getContentHandle() {
		return contentHandle;
	}

	public void addRelationship(IKeyedRelationship relationship) {
		keyedRelationships.add(relationship);
		addRelationship((IRelationship<?>)relationship);
	}

	public void addRelationship(IBoundaryIdentifierRelationship relationship) {
		indirectRelationships.add(relationship);
		addRelationship((IRelationship<?>)relationship);
	}

	public void addRelationship(IRelationship<?> relationship) {
		relationships.add(relationship);
	}
}

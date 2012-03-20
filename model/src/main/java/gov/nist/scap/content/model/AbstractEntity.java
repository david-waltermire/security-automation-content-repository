package gov.nist.scap.content.model;

import gov.nist.scap.content.model.definitions.IEntityDefinition;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public abstract class AbstractEntity<DEFINITION extends IEntityDefinition> implements IMutableEntity<DEFINITION>, IRelationshipVisitor {
	private final DEFINITION contentDefinition;
	private final List<IRelationship<?>> relationships = new LinkedList<IRelationship<?>>();
	private final List<IKeyedRelationship> keyedRelationships = new LinkedList<IKeyedRelationship>();
	private final List<IBoundaryIdentifierRelationship> boundaryIdentifierRelationships = new LinkedList<IBoundaryIdentifierRelationship>();
    private final List<ICompositeRelationship> compositeRelationships = new LinkedList<ICompositeRelationship>();
	private final Map<String, LinkedHashSet<String>> properties = new HashMap<String, LinkedHashSet<String>>();
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

	public List<IBoundaryIdentifierRelationship> getBoundaryIdentifierRelationships() {
		return Collections.unmodifiableList(boundaryIdentifierRelationships);
	}
	
	@Override
	public Collection<? extends ICompositeRelationship> getCompositeRelationships() {
        return Collections.unmodifiableList(compositeRelationships);
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

	public Map<String, LinkedHashSet<String>> getProperties() {
		return Collections.unmodifiableMap(properties);
	}

	public List<String> getPropertyById(String id) {
		Set<String> result = properties.get(id);
		List<String> retval = null;
		if (result != null) {
			retval = new ArrayList<String>(result);
		}
		return retval;
	}

	public void addProperty(String definitionId, String value) {
		if (value == null) {
			throw new NullPointerException("value");
		}
		addProperty(definitionId, Collections.singletonList(value));
	}

	public void addProperty(String definitionId, List<String> values) {
		if (definitionId == null) {
			throw new NullPointerException("definitionId");
		}
		if (values == null) {
			throw new NullPointerException("value");
		} else if (values.isEmpty()) {
			throw new IllegalArgumentException("values is empty");
		}

		LinkedHashSet<String> result = properties.get(definitionId);
		if (result == null) {
			result = new LinkedHashSet<String>();
			properties.put(definitionId, result);
		}
		result.addAll(values);
	}

	public void addRelationship(IRelationship<?> relationship) {
		relationships.add(relationship);
		relationship.accept(this);
	}

	public void visit(ICompositeRelationship relationship) {
	    compositeRelationships.add(relationship);
	}

	public void visit(IBoundaryIdentifierRelationship relationship) {
		boundaryIdentifierRelationships.add(relationship);
	}

	public void visit(IKeyedRelationship relationship) {
		keyedRelationships.add(relationship);
	}
}

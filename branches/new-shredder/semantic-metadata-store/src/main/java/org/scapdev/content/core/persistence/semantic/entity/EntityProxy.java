package org.scapdev.content.core.persistence.semantic.entity;

import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.IContentHandle;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationship;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.IEntityDefinition;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class EntityProxy implements IEntity<IEntityDefinition> {

    private IEntity<?> entity;
    
    public void setIEntity(IEntity<?> e) {
        this.entity = e;
    }
    @Override
    public IEntityDefinition getDefinition() {
        checkState();
        return entity.getDefinition();
    }

    @Override
    public IEntity<?> getParent() {
        checkState();
        return entity.getParent();
    }

    @Override
    public Collection<? extends IRelationship<?>> getRelationships() {
        checkState();
        return entity.getRelationships();
    }

    @Override
    public Collection<? extends IKeyedRelationship> getKeyedRelationships() {
        checkState();
        return entity.getKeyedRelationships();
    }

    @Override
    public Collection<? extends IBoundaryIdentifierRelationship> getBoundaryIdentifierRelationships() {
        checkState();
        return entity.getBoundaryIdentifierRelationships();
    }

    @Override
    public IContentHandle getContentHandle() {
        checkState();
        return entity.getContentHandle();
    }

    @Override
    public void accept(IEntityVisitor visitor) {
        checkState();
        entity.accept(visitor);
    }

    @Override
    public IVersion getVersion() {
        checkState();
        return entity.getVersion();
    }

    @Override
    public Map<String, ? extends Set<String>> getProperties() {
        checkState();
        return entity.getProperties();
    }

    @Override
    public List<String> getPropertyById(String id) {
        checkState();
        return entity.getPropertyById(id);
    }
    
    private void checkState() {
        if( entity == null )
            throw new IllegalStateException("The entity proxy has not be linked to a concrete object.");
    }
    
}

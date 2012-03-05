package gov.nist.scap.content.exist;

import java.io.OutputStream;
import java.util.Collection;

import javax.xml.bind.JAXBElement;

import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.Relationship;

public class ExistDBEntity implements Entity {

    @Override
    public EntityInfo getEntityInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Key getKey() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public JAXBElement<Object> getObject() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void writeOutEntity(OutputStream os) {
        // TODO Auto-generated method stub

    }

    @Override
    public Collection<Relationship> getRelationships() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<KeyedRelationship> getKeyedRelationships() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Collection<IndirectRelationship> getIndirectRelationships() {
        // TODO Auto-generated method stub
        return null;
    }

}

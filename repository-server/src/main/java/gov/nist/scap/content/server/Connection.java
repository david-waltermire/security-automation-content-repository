package gov.nist.scap.content.server;

import java.util.List;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import org.scapdev.content.core.ContentException;
import org.scapdev.content.core.persistence.hybrid.HybridContentPersistenceManager;

public class Connection implements IConnection {

    HybridContentPersistenceManager hcpm;
    
    public Connection(HybridContentPersistenceManager hcpm) {
        this.hcpm = hcpm;
    }
    
    @Override
    public IKeyedEntity<?> getEntityByKey(IKey key) throws ProcessingException {
        return hcpm.getEntityByKey(key);
    }
    
    @Override
    public void storeEntities(
            List<? extends IEntity<?>> entities,
            IMetadataModel model) throws ContentException {
        hcpm.storeEntities(entities, model);
    }
    
    @Override
    public void shutdown() {
        hcpm.shutdown();
    }
}

package gov.nist.scap.content.server;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.List;

import org.scapdev.content.core.ContentException;

public interface IConnection {

    IKeyedEntity<?> getEntityByKey(IKey key) throws ProcessingException;

    void storeEntities(List<? extends IEntity<?>> entities, IMetadataModel model)
            throws ContentException;

    void shutdown();

}

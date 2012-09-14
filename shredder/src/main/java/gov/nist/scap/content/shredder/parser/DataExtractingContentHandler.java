package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.IContentNode;
import gov.nist.scap.content.model.IDocument;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IGeneratedDocument;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedDocument;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.KeyedRelationshipInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO: deal with synchronization issues 'synchronized'
public class DataExtractingContentHandler implements ContentHandler,
		IEntityVisitor {
	private static final Logger log = LoggerFactory
			.getLogger(DataExtractingContentHandler.class);

	private final Collection<KeyedRelationshipInfo> keyedRelationships = new LinkedList<KeyedRelationshipInfo>();
	private final Map<IEntity<?>, Map<IKey, Map<String,IKeyedEntity<?>>>> keyedEntities = new HashMap<IEntity<?>, Map<IKey, Map<String,IKeyedEntity<?>>>>();
	private final Map<IKey, Map<String,IKeyedEntity<?>>> conflictedKeyedEntities = new HashMap<IKey, Map<String,IKeyedEntity<?>>>();

	private final LinkedList<IEntity<?>> entities = new LinkedList<IEntity<?>>();

	private IEntityComparator entityComparator;

	public DataExtractingContentHandler() {
	}

	/**
	 * @return a depth first ordered list of processed entities
	 */
	@Override
	public List<? extends IEntity<?>> getEntities() {
		process();
		return Collections.unmodifiableList(entities);
	}

	public void process() {
		Iterator<KeyedRelationshipInfo> i = keyedRelationships.iterator();
		while (i.hasNext()) {
			KeyedRelationshipInfo info = i.next();
			IKey key = info.getKey();
			IEntity<?> parentE = info.getContainingEntity();
			while( !(parentE instanceof IDocument) ) {
				parentE = parentE.getParent();
			}
			Map<String,IKeyedEntity<?>> version = keyedEntities.get(parentE).get(key);
			if( version == null || version.keySet().size() == 0 ) {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not find an entity matching:\n");
				sb.append(key.getId() + "\n");
				for (Map.Entry<String, String> entry : key
						.getFieldNameToValueMap().entrySet()) {
					sb.append("Field: " + entry.getKey() + " Value: "
							+ entry.getValue() + "\n");
				}
				throw new IllegalArgumentException(
						sb.toString());
			}
			if( version.keySet().size() > 1 ) {
				StringBuffer sb = new StringBuffer();
				sb.append("Could not find a unique entity matching:\n");
				sb.append(key.getId() + "\n");
				for (Map.Entry<String, String> entry : key
						.getFieldNameToValueMap().entrySet()) {
					sb.append("Field: " + entry.getKey() + " Value: "
							+ entry.getValue() + "\n");
				}
				throw new IllegalArgumentException(
						sb.toString());
			}
			IKeyedEntity<?> referencedEntity = version.get(version.keySet().iterator().next());
			if (referencedEntity != null) {
				info.applyRelationship(referencedEntity);
				i.remove();
			} else {
				log.warn("The key '" + key.toString()
						+ "' does not match an entity");
			}
		}
	}

	public void handle(IEntity<?> entity) {
		entity.accept(this);
		entities.add(entity);
	}

	public void handle(KeyedRelationshipInfo info) {
		keyedRelationships.add(info);
	}

	private void addKeyedEntity(IKeyedEntity<?> keyedEntity) {
		IEntity<?> parentE = keyedEntity;
		while( !(parentE instanceof IDocument) ) {
			parentE = parentE.getParent();
		}
		IKey key = keyedEntity.getKey();
		if (key != null) {
			IVersion version = keyedEntity.getVersion();

			if( !conflictedKeyedEntities.containsKey(key) ) {
				conflictedKeyedEntities.put(key, new HashMap<String,IKeyedEntity<?>>());
			}
			
			//Null version is default
			if( conflictedKeyedEntities.get(key).containsKey(version) ) {
				log.warn("Key already exists in document");
				log.warn("Key URI: " + key.getId());
				for (Map.Entry<String, String> entry : key
						.getFieldNameToValueMap().entrySet()) {
					log.warn("Field: " + entry.getKey() + " Value: "
							+ entry.getValue());
				}
				IEntityComparator.STATUS status = entityComparator
						.compareEntities(conflictedKeyedEntities.get(key).get(version), keyedEntity);
				if( status == null ) {
					throw new RuntimeException("Unspecified error comparing entities.");
				}
				
				if (status == IEntityComparator.STATUS.DIFFERENT) {
					throw new IllegalArgumentException(
							"Two entities exist in the source content with the same key, but they aren't similar or identical.");
				} else if( status == IEntityComparator.STATUS.SIMILAR ) {
					log.warn("Using similar entity in same submission.");
				}
				
				//TODO if similiar or identical, need to link up the composite/keyed relationships correctly to avoid duplicates
				
			}

			if( !keyedEntities.containsKey(parentE) ) {
				keyedEntities.put(parentE, new HashMap<IKey, Map<String,IKeyedEntity<?>>>());
			}
			
			if( !keyedEntities.get(parentE).containsKey(key) ) {
				keyedEntities.get(parentE).put(key, new HashMap<String,IKeyedEntity<?>>());
			}
			
			keyedEntities.get(parentE).get(key).put(version == null ? (String)null : version.getValue(), keyedEntity);
		}
	}

	public void visit(IContentNode entity) {
		addKeyedEntity(entity);
	}

	public void visit(IKeyedDocument document) {
		addKeyedEntity(document);
	}

	public void visit(IGeneratedDocument document) {
	}

	public void setEntityComparator(IEntityComparator entityComparator) {
		this.entityComparator = entityComparator;
	}
	
	
}

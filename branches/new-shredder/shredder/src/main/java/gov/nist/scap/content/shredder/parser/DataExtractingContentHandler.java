package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.IContainerVisitor;
import gov.nist.scap.content.model.IContentNode;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IGeneratedDocument;
import gov.nist.scap.content.model.IKeyedDocument;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.KeyedRelationshipInfo;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


// TODO: deal with synchronization issues 'synchronized'
public class DataExtractingContentHandler implements ContentHandler, IContainerVisitor {
	private static final Logger log = Logger.getLogger(DataExtractingContentHandler.class);

	private final Collection<KeyedRelationshipInfo> keyedRelationships = new LinkedList<KeyedRelationshipInfo>();
	private final Map<IKey, IKeyedEntity<?>> keyedEntities = new HashMap<IKey, IKeyedEntity<?>>();
	private final LinkedList<IMutableEntity<?>> entities = new LinkedList<IMutableEntity<?>>();

	public DataExtractingContentHandler() {
	}

	/**
	 * @return a depth first ordered list of processed entities
	 */
	public List<? extends IEntity<?>> getEntities() {
		process();
		return Collections.unmodifiableList(entities);
	}

	public void process() {
		Iterator<KeyedRelationshipInfo> i = keyedRelationships.iterator();
		while (i.hasNext()) {
			KeyedRelationshipInfo info = i.next();
			IKey key = info.getKey();
			IKeyedEntity<?> referencedEntity = keyedEntities.get(key);
			if (referencedEntity != null) {
				info.applyRelationship(referencedEntity);
				i.remove();
			} else {
				log.warn("The key '"+key.toString()+"' does not match an entity");
			}
		}
	}

	public void handle(IMutableEntity<?> entity) {
		entity.accept((IContainerVisitor)this);
		entities.add(entity);
	}

	public void handle(KeyedRelationshipInfo info) {
		keyedRelationships.add(info);
	}

	private void addKeyedEntity(IKeyedEntity<?> keyedEntity) {
		IKey key = keyedEntity.getKey();
		if (key != null) {
			keyedEntities.put(key, keyedEntity);
		}
	}

	public void visit(IContentNode entity) {
		addKeyedEntity(entity);
//
//		XmlCursor cursor = entity.getBookmark().createCursor();
//		StringBuilder builder = new StringBuilder();
//		builder.append("handling content node: ");
//		builder.append(entity.getDefinition().getId());
//		builder.append(": qname=");
//		builder.append(cursor.getName());
//		builder.append(", key=");
//		builder.append(entity.getKey());
//
//		log.debug(builder.toString());
//		cursor.dispose();
	}

	public void visit(IKeyedDocument document) {
		addKeyedEntity(document);
//		XmlCursor cursor = document.getBookmark().createCursor();
//		StringBuilder builder = new StringBuilder();
//		builder.append("Handling indexed document: ");
//		builder.append(document.getDefinition().getId());
//		builder.append(": qname=");
//		builder.append(cursor.getName());
//
//		builder.append(", key=");
//		builder.append(document.getKey());
//
//		log.debug(builder.toString());
//		cursor.dispose();
	}

	public void visit(IGeneratedDocument document) {
//		XmlCursor cursor = document.getBookmark().createCursor();
//		StringBuilder builder = new StringBuilder();
//		builder.append("Handling generated document: ");
//		builder.append(document.getDefinition().getId());
//		builder.append(": qname=");
//		builder.append(cursor.getName());
//
//		log.debug(builder.toString());
//		cursor.dispose();
	}
}

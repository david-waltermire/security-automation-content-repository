package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.model.DefaultCompositeRelationship;
import gov.nist.scap.content.model.DefaultContentNode;
import gov.nist.scap.content.model.DefaultGeneratedDocument;
import gov.nist.scap.content.model.DefaultKeyedDocument;
import gov.nist.scap.content.model.DefaultBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.DefaultVersion;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IContentHandle;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IMutableContentNode;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.IMutableGeneratedDocument;
import gov.nist.scap.content.model.IMutableKeyedDocument;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.KeyException;
import gov.nist.scap.content.model.definitions.ContentMapping;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IContentNodeDefinition;
import gov.nist.scap.content.model.definitions.IDocumentDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinitionVisitor;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IExternalIdentifierMapping;
import gov.nist.scap.content.model.definitions.IGeneratedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IKeyDefinition;
import gov.nist.scap.content.model.definitions.IKeyRefDefinition;
import gov.nist.scap.content.model.definitions.IKeyedDocumentDefinition;
import gov.nist.scap.content.model.definitions.IKeyedField;
import gov.nist.scap.content.model.definitions.IKeyedRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IRelationshipDefinitionVisitor;
import gov.nist.scap.content.model.definitions.IVersionDefinition;
import gov.nist.scap.content.model.definitions.KeyedRelationshipInfo;
import gov.nist.scap.content.model.definitions.ProcessingException;
import gov.nist.scap.content.model.definitions.XPathRetriever;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlRuntimeException;

public class ContentProcessor {
	private static final Logger log = Logger.getLogger(ContentProcessor.class);

	private final ContentHandler contentHandler;

	public ContentProcessor(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	public void process(IDocumentDefinition definition, XmlCursor cursor) throws ProcessingException {
		definition.accept(new ContentProcessingEntityDefinitionVisitor(cursor, null));
	}

	private void processEntity(IMutableEntity<?> entity,
			XmlCursor cursor) throws ProcessingException {

		IVersionDefinition versionDef = entity.getDefinition().getVersionDefinition();
		if (versionDef != null) {
			IVersion version = processVersion(versionDef, cursor);
			if (version == null && versionDef.isUseParentVersionWhenUndefined()) {
				version = entity.getParent().getVersion();
			}
			if (version != null) {
				entity.setVersion(version);
			}
		}
		processRelationships(entity, cursor);

		contentHandler.handle(entity);
	}

	private static IContentHandle getContentHandle(XmlCursor cursor) {
		Bookmark bookmark = new Bookmark();
		cursor.setBookmark(bookmark);

		return new BookmarkContentHandle(bookmark);
	}

	private void processRelationships(IMutableEntity<?> entity,
			XmlCursor cursor) throws ProcessingException {
		Collection<? extends IRelationshipDefinition> relationshipDefinitions = entity.getDefinition().getRelationshipDefinitions();

		if (!relationshipDefinitions.isEmpty()) {
			ContentProcessingRelationshipDefinitionVisitor visitor = new ContentProcessingRelationshipDefinitionVisitor(entity);

			// Handle all relationships
			for (IRelationshipDefinition relationship : relationshipDefinitions) {
				String xpath = relationship.getXpath();
				if (xpath != null) {
					cursor.push();
					try {
						// select the children of this boundary
						cursor.selectPath(xpath);
						int count = cursor.getSelectionCount();
						// iterate over the selected nodes
						for (int i = 0; i < count; i++) {
							if (!cursor.toSelection(i)) {
								throw new RuntimeException("unable to navigate to next cursor position");
							}
							XmlCursor newCursor = cursor.newCursor();
							visitor.setXmlCursor(newCursor);
							relationship.accept(visitor);
						}
					} catch (XmlRuntimeException e) {
						throw new ProcessingException("Unable to select xpath for boundary: "+relationship.getId(), e);
					} finally {
						cursor.pop();
					}
				} else {
					visitor.setXmlCursor(cursor);
					relationship.accept(visitor);
				}
			}
		}
	}

	private static IKey getKey(IKeyDefinition keyDefinition, IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ProcessingException {
		List<? extends IKeyedField> fields = keyDefinition.getFields();

		Map<String, String> fieldIdToValueMap = getFieldValues(fields, parentContext, cursor);
		KeyBuilder builder = new KeyBuilder(fields);
		builder.setId(keyDefinition.getId());
		builder.addFields(fieldIdToValueMap);
		return builder.toKey();
	}

	public static IKey getKey(IKeyRefDefinition keyRefDefinition, IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ProcessingException {
		List<? extends IKeyedField> fields = keyRefDefinition.getFields();
		Map<String, String> referenceFieldIdToValueMap = getFieldValues(fields, parentContext, cursor);

		IKeyDefinition keyDefinition = keyRefDefinition.getKeyDefinition();
		KeyBuilder builder = new KeyBuilder(keyDefinition.getFields());
		builder.setId(keyDefinition.getId());
		builder.addFields(referenceFieldIdToValueMap);
		return builder.toKey();
	}

	private static LinkedHashMap<String, String> getFieldValues(List<? extends IKeyedField> fields, IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ProcessingException {
		LinkedHashMap<String, String> fieldIdToValueMap = new LinkedHashMap<String, String>();

		for (IKeyedField field : fields) {
			String id = field.getName();
			String value = field.getValue(parentContext, cursor);

			if (value == null) {
				throw new KeyException("null field '" + id + "'");
			}
			fieldIdToValueMap.put(id, value);
		}
		return fieldIdToValueMap;
	}

	private static IVersion processVersion(IVersionDefinition version, XmlCursor cursor) {
		XPathRetriever valueRetriever = version.getXpath();
		String value = valueRetriever.getValue(cursor);
		return (value == null ? null : new DefaultVersion(version, value));
	}

	private class ContentProcessingRelationshipDefinitionVisitor implements IRelationshipDefinitionVisitor {
		private final IMutableEntity<?> entity;
		private XmlCursor cursor;

		public ContentProcessingRelationshipDefinitionVisitor(IMutableEntity<?> entity) {
			this.entity = entity;
		}

		public void setXmlCursor(XmlCursor newCursor) {
			this.cursor = newCursor;
		}

		@Override
		public void visit(ICompositeRelationshipDefinition definition) throws ProcessingException {
			ContentMapping contentMapping = definition.getContentMapping();
			QName qname = cursor.getName();
			// retrieve the content definition to use to process the child node
			IEntityDefinition contentDefinition = contentMapping.getContentDefinitionForQName(qname);
			if (contentDefinition == null) {
				log.warn("Unrecognized QName '"+qname.toString()+"' at boundary: "+definition.getId());
			} else {
				// process the child node
				IMutableEntity<?> childEntity = contentDefinition.accept(new ContentProcessingEntityDefinitionVisitor(cursor, entity));

				ICompositeRelationship relationship = new DefaultCompositeRelationship(definition, childEntity, entity);
				// TODO: determine which to keep
				entity.addRelationship(relationship);
				childEntity.addRelationship(relationship);
			}
		}

		public void visit(IBoundaryIdentifierRelationshipDefinition definition) {
			XPathRetriever valueRetriever = definition.getValueRetriever();
			IExternalIdentifierMapping qualifierMapping = definition.getQualifierMapping();

			String value = valueRetriever.getValue(cursor);
			IExternalIdentifier externalIdentifier = qualifierMapping.resolveExternalIdentifier(cursor);
			if (externalIdentifier != null) {
				entity.addRelationship(new DefaultBoundaryIdentifierRelationship(definition, entity, externalIdentifier, value));
			} else {
				log.warn("Unable to extract indirect relationship for value: "+value);
			}
		}

		public void visit(IKeyedRelationshipDefinition definition) throws ProcessingException {
			IKey key;
			try {
				key = getKey(definition.getKeyRefDefinition(), entity, cursor);
			} catch (KeyException e) {
				throw new ProcessingException("Unable to extract key for keyed relationship '"+definition.getId()+"' on entity: "+entity.getDefinition().getId(), e);
			}
			contentHandler.handle(new KeyedRelationshipInfo(definition, entity, key));
		}
		
	}

	private class ContentProcessingEntityDefinitionVisitor implements IEntityDefinitionVisitor<IMutableEntity<?>> {
		private final XmlCursor cursor;
		private final IMutableEntity<?> parent;

		public ContentProcessingEntityDefinitionVisitor(XmlCursor cursor,
				IMutableEntity<?> parent) {
			this.cursor = cursor;
			this.parent = parent;
		}

		public IMutableEntity<?> visit(IGeneratedDocumentDefinition definition) throws ProcessingException {
			IMutableGeneratedDocument document = new DefaultGeneratedDocument(definition, getContentHandle(cursor), parent);
			processEntity(document, cursor);
			return document;
		}

		public IMutableEntity<?> visit(IKeyedDocumentDefinition definition) throws ProcessingException {
			IKeyDefinition keyDefinition = definition.getKeyDefinition();
			IKey key;
			try {
				key = getKey(keyDefinition, parent, cursor);
			} catch (KeyException e) {
				throw new ProcessingException("Unable to extract key for entity: "+cursor.getName().toString(), e);
			}

			IMutableKeyedDocument document = new DefaultKeyedDocument(definition, key, getContentHandle(cursor), parent);

			IVersionDefinition versionDef = definition.getVersionDefinition();
			if (versionDef != null) {
				IVersion version = processVersion(versionDef, cursor);
				if (version != null) {
					document.setVersion(version);
				}
			}
			processEntity(document, cursor);
			return document;
		}

		public IMutableEntity<?> visit(IContentNodeDefinition definition) throws ProcessingException {
			IKeyDefinition keyDefinition = definition.getKeyDefinition();
			IKey key;
			try {
				key = getKey(keyDefinition, parent, cursor);
			} catch (KeyException e) {
				throw new ProcessingException("Unable to extract key for entity: "+cursor.getName().toString(), e);
			}

			IMutableContentNode node = new DefaultContentNode(definition, key, getContentHandle(cursor), parent);
			processEntity(node, cursor);
			return node;
		}
		
	}
}

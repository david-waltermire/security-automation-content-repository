package gov.nist.scap.content.shredder.parser;

import gov.nist.scap.content.definitions.ContentMapping;
import gov.nist.scap.content.definitions.IBoundaryRelationshipDefinition;
import gov.nist.scap.content.definitions.IContentNodeDefinition;
import gov.nist.scap.content.definitions.IDocumentDefinition;
import gov.nist.scap.content.definitions.IEntityDefinition;
import gov.nist.scap.content.definitions.IEntityDefinitionVisitor;
import gov.nist.scap.content.definitions.IExternalIdentifier;
import gov.nist.scap.content.definitions.IExternalIdentifierMapping;
import gov.nist.scap.content.definitions.IGeneratedDocumentDefinition;
import gov.nist.scap.content.definitions.IIndirectRelationshipDefinition;
import gov.nist.scap.content.definitions.IKeyDefinition;
import gov.nist.scap.content.definitions.IKeyedDocumentDefinition;
import gov.nist.scap.content.definitions.IKeyedRelationshipDefinition;
import gov.nist.scap.content.definitions.IRelationshipDefinition;
import gov.nist.scap.content.definitions.IRelationshipDefinitionVisitor;
import gov.nist.scap.content.definitions.KeyedRelationshipInfo;
import gov.nist.scap.content.definitions.ProcessingException;
import gov.nist.scap.content.definitions.XPathRetriever;
import gov.nist.scap.content.model.Bookmark;
import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.DefaultBoundaryRelationship;
import gov.nist.scap.content.model.DefaultContentNode;
import gov.nist.scap.content.model.DefaultGeneratedDocument;
import gov.nist.scap.content.model.DefaultIndexedDocument;
import gov.nist.scap.content.model.DefaultIndirectRelationship;
import gov.nist.scap.content.model.IBoundaryRelationship;
import gov.nist.scap.content.model.IContentHandle;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IMutableContentNode;
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.IMutableGeneratedDocument;
import gov.nist.scap.content.model.IMutableKeyedDocument;
import gov.nist.scap.content.model.KeyException;

import java.util.Collection;

import javax.xml.namespace.QName;

import org.apache.log4j.Logger;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.XmlBookmark;
import org.apache.xmlbeans.XmlRuntimeException;

public class ContentProcessor {
	private static final Logger log = Logger.getLogger(ContentProcessor.class);

	private final ContentHandler contentHandler;

	public ContentProcessor(ContentHandler contentHandler) {
		this.contentHandler = contentHandler;
	}

	public void process(IDocumentDefinition definition, XmlCursor cursor) throws ContentException, ProcessingException {
		definition.accept(new ContentProcessingEntityDefinitionVisitor(cursor, null));
	}

	private void processEntity(IMutableEntity<?> entity,
			XmlCursor cursor) throws ProcessingException, ContentException {
		processRelationships(entity, cursor);

		contentHandler.handle(entity);
	}

	private IContentHandle getContentHandle(XmlCursor cursor) {
		Bookmark bookmark = new Bookmark();
		cursor.setBookmark(bookmark);

		return new ContentHandle(bookmark);
	}

	private void processRelationships(IMutableEntity<?> entity,
			XmlCursor cursor) throws ProcessingException, ContentException {
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

	private static class ContentHandle implements IContentHandle {
		private final XmlCursor.XmlBookmark bookmark;

		public ContentHandle(XmlCursor.XmlBookmark bookmark) {
			this.bookmark = bookmark;
		}

		public XmlCursor getCursor() {
			return bookmark.createCursor();
		}

		public XmlBookmark getBookmark() {
			return bookmark;
		}
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
		public void visit(IBoundaryRelationshipDefinition definition) throws ContentException, ProcessingException {
			ContentMapping contentMapping = definition.getContentMapping();
			QName qname = cursor.getName();
			// retrieve the content definition to use to process the child node
			IEntityDefinition contentDefinition = contentMapping.getContentDefinitionForQName(qname);
			if (contentDefinition == null) {
				log.warn("Unrecognized QName '"+qname.toString()+"' at boundary: "+definition.getId());
			} else {
				// process the child node
				IMutableEntity<?> childEntity = contentDefinition.accept(new ContentProcessingEntityDefinitionVisitor(cursor, entity));

				IBoundaryRelationship relationship = new DefaultBoundaryRelationship(definition, childEntity, entity);
				// TODO: determine which to keep
				entity.addRelationship(relationship);
				childEntity.addRelationship(relationship);
			}
		}

		public void visit(IIndirectRelationshipDefinition definition) {
			XPathRetriever valueRetriever = definition.getValueRetriever();
			IExternalIdentifierMapping qualifierMapping = definition.getQualifierMapping();

			String value = valueRetriever.getValue(cursor);
			IExternalIdentifier externalIdentifier = qualifierMapping.resolveExternalIdentifier(cursor);
			if (externalIdentifier != null) {
				entity.addRelationship(new DefaultIndirectRelationship(definition, entity, externalIdentifier, value));
			} else {
				log.warn("Unable to extract indirect relationship for value: "+value);
			}
		}

		@Override
		public void visit(IKeyedRelationshipDefinition definition) throws KeyException, ContentException {
			IKey key = definition.getKeyRefDefinition().getKey(entity, cursor);
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

		public IMutableEntity<?> visit(IGeneratedDocumentDefinition definition) throws ContentException, ProcessingException {
			IMutableGeneratedDocument document = new DefaultGeneratedDocument(definition, getContentHandle(cursor), parent);
			processEntity(document, cursor);
			return document;
		}

		public IMutableEntity<?> visit(IKeyedDocumentDefinition definition) throws ContentException, ProcessingException {
			IKeyDefinition keyDefinition = definition.getKeyDefinition();
			IKey key = keyDefinition.getKey(parent, cursor);

			IMutableKeyedDocument document = new DefaultIndexedDocument(definition, key, getContentHandle(cursor), parent);
			processEntity(document, cursor);
			return document;
		}

		public IMutableEntity<?> visit(IContentNodeDefinition definition) throws KeyException, ContentException, ProcessingException {
			IKeyDefinition keyDefinition = definition.getKeyDefinition();
			IKey key = keyDefinition.getKey(parent, cursor);

			IMutableContentNode node = new DefaultContentNode(definition, key, getContentHandle(cursor), parent);
			processEntity(node, cursor);
			return node;
		}
		
	}
}

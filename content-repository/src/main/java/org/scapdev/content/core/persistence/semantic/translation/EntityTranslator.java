/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 paul
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
/**
 * 
 */
package org.scapdev.content.core.persistence.semantic.translation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;

import org.apache.log4j.Logger;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.semantic.MetaDataOntology;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.ExternalIdentifier;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.Relationship;

/**
 * Translates between Entitys
 */
public class EntityTranslator extends
		AbstractSemanticTranslator<Entity> implements
		SemanticTranslator<Entity> {
	private static final Logger log = Logger.getLogger(EntityTranslator.class);

	private MetaDataOntology ontology;
	
	
	
	/**
	 * 
	 * @param baseURI - - the base URI to use for all RDF individuals produced by this translator
	 * @param ontology
	 * @param factory
	 */
	public EntityTranslator(String baseURI, MetaDataOntology ontology, ValueFactory factory) {
		super(baseURI, factory);
		this.ontology = ontology;
	}

	@Override
	public Entity translateToJava(List<Statement> statements, MetadataModel model, ContentRetrieverFactory contentRetrieverFactory) {
		InternalEntity descriptor = new InternalEntity();
		
		// key = boundary_boject_id
		Map<String, IndirectRelationship> indirectRelationships = new HashMap<String, IndirectRelationship>();
		
		for (Statement statement : statements){
			URI predicate = statement.getPredicate();
			if (predicate.equals(ontology.HAS_CONTENT_ID.URI)){
				String contentId = statement.getObject().stringValue();
				descriptor.setRetriever((contentRetrieverFactory.newContentRetriever(contentId, model)));
			}
			if (predicate.equals(ontology.HAS_INDIRECT_RELATIONSHIP_TO)){
				String boundaryObjectURI = statement.getObject().stringValue();
				IndirectRelationship indirectRel = indirectRelationships.get(boundaryObjectURI);
				if (indirectRel == null){
//					indirectRel = new 
				}
				
				
				
				
				
			}
			if (predicate.equals(ontology.HAS_BOUNDARY_OBJECT_TYPE.URI)){
				
			}
		}
		return null;
	}

	@Override
	public List<Statement> translateToRdf(Entity entity, String contentId) {
		List<Statement> statements = new LinkedList<Statement>();
//		log.info("about to add triples");
		
		// first handle the basic entity assertion
		URI entityUri = genInstanceURI(contentId);
		statements.add(factory.createStatement(entityUri, RDF.TYPE, ontology.ENTITY_CLASS.URI));
		statements.add(factory.createStatement(entityUri, RDFS.LABEL, factory.createLiteral(contentId)));
		statements.add(factory.createStatement(entityUri, ontology.HAS_CONTENT_ID.URI, factory.createLiteral(contentId)));
		
		// now handle the key of the entity, right now I see no reason to not use bnodes....that may change
		BNode key = factory.createBNode();
		
		statements.add(factory.createStatement(key, RDF.TYPE, ontology.KEY_CLASS.URI));
		statements.add(factory.createStatement(entityUri, ontology.HAS_KEY.URI, key));
		statements.add(factory.createStatement(key, ontology.HAS_KEY_TYPE.URI, factory.createLiteral(entity.getKey().getId())));
		for (Map.Entry<String, String> keyFieldEntry : entity.getKey().getIdToValueMap().entrySet()){
			BNode keyField = factory.createBNode();
			statements.add(factory.createStatement(keyField, RDF.TYPE, ontology.FIELD_CLASS.URI));
			statements.add(factory.createStatement(key, ontology.HAS_FIELD_DATA.URI, keyField));
			statements.add(factory.createStatement(keyField, ontology.HAS_FIELD_TYPE.URI, factory.createLiteral(keyFieldEntry.getKey())));
			statements.add(factory.createStatement(keyField, ontology.HAS_FIELD_VALUE.URI, factory.createLiteral(keyFieldEntry.getValue())));
		}
		
		
		// now handle all relationship information
		
		// handle indirect relationships first
		for (IndirectRelationship relationship : entity.getIndirectRelationships()) {
			ExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
			URI boundaryObjectURI = genInstanceURI(externalIdentifier.getValue());
			statements.add(factory.createStatement(boundaryObjectURI, RDF.TYPE, ontology.BOUNDARY_OBJECT_CLASS.URI));
			statements.add(factory.createStatement(boundaryObjectURI, RDFS.LABEL, factory.createLiteral(externalIdentifier.getValue())));
			statements.add(factory.createStatement(boundaryObjectURI, ontology.HAS_BOUNDARY_OBJECT_TYPE.URI, factory.createLiteral(externalIdentifier.getId())));
			statements.add(factory.createStatement(entityUri, ontology.HAS_INDIRECT_RELATIONSHIP_TO.URI, boundaryObjectURI));
		}
		
		// handle keyed relationships
		for (KeyedRelationship relationship : entity.getKeyedRelationships()){
//			URI relatedEntityURI = findEntityURI(relationship.getKey());
//			if (relatedEntityURI == null){
//				//TODO: need to callback to caller to get actual entity... or queue for later
//			}
//			statements.add(factory.createStatement(entityUri, ontology.HAS_DIRECT_RELATIONSHIP_TO.URI, relatedEntityURI));
			//add symmetric side of this as well until inferencing is hooked up
//			statements.add(factory.createStatement(relatedEntityURI, ontology.HAS_DIRECT_RELATIONSHIP_TO.URI, entityUri));
		}
		
		return statements;
	}
	
	private static class IndirectRelationshipBuilder{
		// this is used to find the IndirectRelationshipInfo type
		private String externalIdType;
		private String externalIdValue;
		
		public IndirectRelationshipBuilder() {
			// TODO Auto-generated constructor stub
		}
	}

	private static class InternalEntity implements Entity {
		private Key key;
		private EntityInfo entityInfo;
		private Collection<Relationship> relationships;
		private Collection<KeyedRelationship> keyedRelationships;
		private Collection<IndirectRelationship> indirectRelationships;
		private ContentRetriever retriever;

		public InternalEntity() {
		}

		@Override
		public Key getKey() {
			return key;
		}

		/**
		 * @return the entityInfo
		 */
		public EntityInfo getEntityInfo() {
			return entityInfo;
		}

		@Override
		public Collection<Relationship> getRelationships() {
			return relationships;
		}

		@Override
		public Collection<IndirectRelationship> getIndirectRelationships() {
			return indirectRelationships;
		}

		@Override
		public Collection<KeyedRelationship> getKeyedRelationships() {
			return keyedRelationships;
		}

		@Override
		public JAXBElement<Object> getObject() {
			return retriever.getContent();
		}
		
		void setRetriever(ContentRetriever retriever) {
			this.retriever = retriever;
		}

	}

}

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

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.semantic.MetaDataOntology;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.ExternalIdentifier;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.MetadataModel;

/**
 * Translates entities accross the different modeling languages.
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
		List<RegenerationStatementManager> managers = new LinkedList<RegenerationStatementManager>(); 
		managers.add(new IndirectRelationshipStatementManager(ontology, model));
		managers.add(new KeyStatementManager(ontology));
		
		RebuiltEntity target = new RebuiltEntity();
		
		for (Statement statement : statements){
			URI predicate = statement.getPredicate();
			//first handle entity specific predicates
			if (predicate.equals(ontology.HAS_CONTENT_ID.URI)){
				String contentId = statement.getObject().stringValue();
				target.setRetriever((contentRetrieverFactory.newContentRetriever(contentId, model)));
				continue;
			}
			if (predicate.equals(ontology.HAS_ENTITY_TYPE.URI)){
				String entityType = statement.getObject().stringValue();
				target.setEntityInfo(model.getEntityById(entityType));
			}
			//now handle rest of graph
			for (RegenerationStatementManager statementManager : managers){
				if (statementManager.scan(statement)){
					continue;
				}
			}
		}
		for (RegenerationStatementManager statementManager : managers){
			statementManager.populateEntity(target);
		}

		return target;
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
		statements.add(factory.createStatement(entityUri, ontology.HAS_ENTITY_TYPE.URI, factory.createLiteral(entity.getEntityInfo().getId())));
		
		
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
			String relationshipId = relationship.getRelationshipInfo().getId();
			ExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
			String boundaryObjectValue = externalIdentifier.getValue();
			URI boundaryObjectURI = genInstanceURI(boundaryObjectValue);
			statements.add(factory.createStatement(boundaryObjectURI, RDF.TYPE, ontology.BOUNDARY_OBJECT_CLASS.URI));
			statements.add(factory.createStatement(boundaryObjectURI, RDFS.LABEL, factory.createLiteral(externalIdentifier.getValue())));
			statements.add(factory.createStatement(boundaryObjectURI, ontology.HAS_BOUNDARY_OBJECT_TYPE.URI, factory.createLiteral(externalIdentifier.getId())));
			statements.add(factory.createStatement(boundaryObjectURI, ontology.HAS_BOUNDARY_OBJECT_VALUE.URI, factory.createLiteral(boundaryObjectValue)));
			statements.add(factory.createStatement(entityUri, ontology.findIndirectRelationshipURI(relationshipId), boundaryObjectURI));
		}
		
		// handle keyed relationships
		for (KeyedRelationship relationship : entity.getKeyedRelationships()){
//			URI relatedEntityURI = genInstanceURI(relationship.getRelatedEntity())
//			if (relatedEntityURI == null){
//				//TODO: need to callback to caller to get actual entity... or queue for later
//			}
//			statements.add(factory.createStatement(entityUri, ontology.HAS_DIRECT_RELATIONSHIP_TO.URI, relatedEntityURI));
//			add symmetric side of this as well until inferencing is hooked up
//			statements.add(factory.createStatement(relatedEntityURI, ontology.HAS_DIRECT_RELATIONSHIP_TO.URI, entityUri));
		}
		
		return statements;
	}
	



}

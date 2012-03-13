/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 Paul Cichonski
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

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IIndirectRelationship;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.semantic.MetaDataOntology;

/**
 * Translates entities across the different modeling languages.
 */
public class EntityTranslator extends
		AbstractSemanticTranslator{
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

	/**
	 * 
	 * @param statements
	 *            - all statements to constitute entity
	 * @param relatedEntityStatements
	 *            - all statement to constitute relatedEntityKeys from
	 *            KeyedRelationships
	 * @param model
	 * @param contentRetrieverFactory
	 * @return
	 */
	public IEntity<?> translateToJava(Set<Statement> statements, Map<URI, IKey> relatedEntityKeys, IMetadataModel model, ContentRetrieverFactory contentRetrieverFactory) {
		List<RegenerationStatementManager> managers = new LinkedList<RegenerationStatementManager>(); 
		managers.add(new IndirectRelationshipStatementManager(ontology, model));
		managers.add(new KeyedRelationshipStatementManager(ontology, model, factory, relatedEntityKeys));
		managers.add(new KeyStatementManager(ontology));
		// TODO: determine the entity type before construction
		RebuiltEntity<?> target = new RebuiltEntity();
		for (Statement statement : statements){
			URI predicate = statement.getPredicate();
			//first handle entity specific predicates
			if (predicate.equals(ontology.HAS_CONTENT_ID.URI)){
				String contentId = statement.getObject().stringValue();
				target.setContentHandle((contentRetrieverFactory.newContentRetriever(contentId, model)));
				continue;
			}
			if (predicate.equals(ontology.HAS_ENTITY_TYPE.URI)){
				String entityType = statement.getObject().stringValue();
				target.setEntityInfo(model.getEntityInfoById(entityType));
				continue;
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
	

	/**
	 * <p>
	 * Will return the partial graph of the RDF entity. Client will have to have
	 * to complete all Incomplete Statements from the Partial Graph...which is
	 * connecting the direct relationship from the passed in Entity to the
	 * target entity.
	 * </p>
	 * 
	 * @param entity
	 * @param contentId
	 * @return
	 */
	public PartialEntityGraph translateToRdf(IEntity<?> entity, String contentId) {
		PartialEntityGraph target = new PartialEntityGraph();
//		log.info("about to add triples");
		
		// first handle the basic entity assertion
		URI entityUri = genInstanceURI(contentId);
		target.add(factory.createStatement(entityUri, RDF.TYPE, ontology.ENTITY_CLASS.URI));
		//for now just use first key value as label
		target.add(factory.createStatement(entityUri, RDFS.LABEL, factory.createLiteral(entity.getKey().getIdToValueMap().values().iterator().next())));
		target.add(factory.createStatement(entityUri, ontology.HAS_CONTENT_ID.URI, factory.createLiteral(contentId)));
		target.add(factory.createStatement(entityUri, ontology.HAS_ENTITY_TYPE.URI, factory.createLiteral(entity.getDefinition().getId())));
		
		
		// now handle the key of the entity, right now I see no reason to not use bnodes....that may change
		BNode key = factory.createBNode();
		
		target.add(factory.createStatement(key, RDF.TYPE, ontology.KEY_CLASS.URI));
		target.add(factory.createStatement(entityUri, ontology.HAS_KEY.URI, key));
		target.add(factory.createStatement(key, ontology.HAS_KEY_TYPE.URI, factory.createLiteral(entity.getKey().getId())));
		for (Map.Entry<String, String> keyFieldEntry : entity.getKey().getIdToValueMap().entrySet()){
			BNode keyField = factory.createBNode();
			target.add(factory.createStatement(keyField, RDF.TYPE, ontology.FIELD_CLASS.URI));
			target.add(factory.createStatement(key, ontology.HAS_FIELD_DATA.URI, keyField));
			target.add(factory.createStatement(keyField, ontology.HAS_FIELD_TYPE.URI, factory.createLiteral(keyFieldEntry.getKey())));
			target.add(factory.createStatement(keyField, ontology.HAS_FIELD_VALUE.URI, factory.createLiteral(keyFieldEntry.getValue())));
		}
		// now handle all relationship information
		
		// handle indirect relationships first
		for (IIndirectRelationship relationship : entity.getIndirectRelationships()) {
			String relationshipId = relationship.getDefinition().getId();
			IExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
			String boundaryObjectValue = relationship.getValue();
			URI boundaryObjectURI = genInstanceURI(boundaryObjectValue);
			target.add(factory.createStatement(boundaryObjectURI, RDF.TYPE, ontology.BOUNDARY_OBJECT_CLASS.URI));
			target.add(factory.createStatement(boundaryObjectURI, RDFS.LABEL, factory.createLiteral(boundaryObjectValue)));
			target.add(factory.createStatement(boundaryObjectURI, ontology.HAS_BOUNDARY_OBJECT_TYPE.URI, factory.createLiteral(externalIdentifier.getId())));
			target.add(factory.createStatement(boundaryObjectURI, ontology.HAS_BOUNDARY_OBJECT_VALUE.URI, factory.createLiteral(boundaryObjectValue)));
			//assert this since inference may not be turned on
			target.add(factory.createStatement(entityUri, ontology.HAS_INDIRECT_RELATIONSHIP_TO.URI, boundaryObjectURI));
			target.add(factory.createStatement(entityUri, ontology.findIndirectRelationshipURI(relationshipId), boundaryObjectURI));
		}
		
		// handle keyed relationships
		for (IKeyedRelationship relationship : entity.getKeyedRelationships()){
			String relationshipId = relationship.getDefinition().getId();
			//assert this since inference may not be turned on
			target.add(entityUri, ontology.HAS_DIRECT_RELATIONSHIP_TO.URI, relationship.getKey());
			// adding incomplete statement to be completed later
			target.add(entityUri, ontology.findDirectRelationshipURI(relationshipId), relationship.getKey());
		}
		return target;
	}
	



}

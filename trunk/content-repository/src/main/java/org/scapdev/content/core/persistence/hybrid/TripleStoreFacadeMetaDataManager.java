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
package org.scapdev.content.core.persistence.hybrid;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.ExternalIdentifier;
import org.scapdev.content.model.ExternalIdentifierInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.Relationship;

/**
 * At this point this is just going to be a facade into the triple store REST interfaces
 *
 */
public class TripleStoreFacadeMetaDataManager implements MetadataStore {
	private static final Logger log = Logger.getLogger(TripleStoreFacadeMetaDataManager.class);
	private static final String BASE_URI = "http://scap.nist.gov/cms/individuals#";
	
	//HACK, REMOVE ONCE TRIPLE STORE HANDLES
	private final Map<Key, EntityDescriptor> descriptorMap = new HashMap<Key, EntityDescriptor>();
	private final Map<String, Map<String, List<EntityDescriptor>>> externalIdentifierToValueMap = new HashMap<String, Map<String, List<EntityDescriptor>>>();;

	private Repository repository;
	
	private ValueFactory factory;
	
	private MetaDataOntology ontology;
	
	private boolean modelLoaded = false;
	
	private boolean x = false;
	
	//******************* model instances ****************************

	
	public TripleStoreFacadeMetaDataManager() {
		//NOTE: this type is non-inferencing, see http://www.openrdf.org/doc/sesame2/2.3.2/users/ch08.html for more detail
		try {
			repository = new SailRepository(new MemoryStore());
			repository.initialize();
			factory = repository.getValueFactory();
			ontology = new MetaDataOntology(factory);
		} catch (RepositoryException e){
			log.error("Exception iniitalizing triple store", e);
		}
	}
	
	@Override
	public EntityDescriptor getEntityDescriptor(Key key) {
		//HACK, REMOVE ONCE TRIPLE STORE HANDLES
		if (!x){
			//uncomment to see what triples were added upon initial data load
//			outputContents();
			x = true;
		}
		return descriptorMap.get(key);
		
	}
	
	@Override
	public List<EntityDescriptor> getEntityDescriptor(ExternalIdentifierInfo externalIdentifierInfo, String value) {
		//HACK, REMOVE ONCE TRIPLE STORE HANDLES
		Map<String, List<EntityDescriptor>> externalIdentifierValueToEntityDescriptorMap = externalIdentifierToValueMap.get(externalIdentifierInfo.getId());
		List<EntityDescriptor> result;
		if (externalIdentifierValueToEntityDescriptorMap.containsKey(value)) {
			result = Collections.unmodifiableList(externalIdentifierValueToEntityDescriptorMap.get(value));
		} else {
			result = Collections.emptyList();
		}
		return result;
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityType) {
		//HACK, REMOVE ONCE TRIPLE STORE HANDLES
		Set<Key> result = Collections.emptySet();
		Map<String, List<EntityDescriptor>> indirectValueToDescriptorsMap = externalIdentifierToValueMap.get(indirectType);
		if (indirectValueToDescriptorsMap != null) {
			result = new HashSet<Key>();
			for (String indirectId : indirectIds) {
				List<EntityDescriptor> descs = indirectValueToDescriptorsMap.get(indirectId);
				if (descs != null) {
					for (EntityDescriptor desc : descs) {
						if (entityType.contains(desc.getEntityInfo().getId())) {
							result.add(desc.getKey());
						}
					}
				}
			}
		}
		return result;
	}

	@Override
	public void persist(Entity entity, String contentId) {
		try {
			RepositoryConnection conn = repository.getConnection();
			try {
				if (!modelLoaded){
					ontology.loadModel(conn);
					modelLoaded = true;
				}
				List<Statement> statements = new LinkedList<Statement>();
				log.info("about to add triples");
				
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
				for (Map.Entry<String, String> keyFields : entity.getKey().getIdToValueMap().entrySet()){
					BNode keyField = factory.createBNode();
					statements.add(factory.createStatement(keyField, RDF.TYPE, ontology.FIELD_CLASS.URI));
					statements.add(factory.createStatement(key, ontology.HAS_FIELD_DATA.URI, keyField));
					statements.add(factory.createStatement(keyField, ontology.HAS_FIELD_TYPE.URI, factory.createLiteral(keyFields.getKey())));
					statements.add(factory.createStatement(keyField, ontology.HAS_FIELD_VALUE.URI, factory.createLiteral(keyFields.getValue())));
				}
				
				
				// now handle all relationship information
				
				// handle indirect relationships first
				for (IndirectRelationship relationship : entity.getIndirectRelationships()) {
					ExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
					URI boundaryObjectURI = genInstanceURI(externalIdentifier.getValue());
					statements.add(factory.createStatement(boundaryObjectURI, RDF.TYPE, ontology.BOUNDARY_OBJECT_CLASS.URI));
					statements.add(factory.createStatement(boundaryObjectURI, RDFS.LABEL, factory.createLiteral(externalIdentifier.getValue())));
					statements.add(factory.createStatement(entityUri, ontology.HAS_INDIRECT_RELATIONSHIP_TO.URI, boundaryObjectURI));
				}
				
				// handle keyed relationships
				for (KeyedRelationship relationship : entity.getKeyedRelationships()){
					URI relatedEntityURI = findEntityURI(relationship.getKey());
					if (relatedEntityURI == null){
						//TODO: need to callback to caller to get actual entity... or queue for later
					}
//					statements.add(factory.createStatement(entityUri, ontology.HAS_DIRECT_RELATIONSHIP_TO.URI, relatedEntityURI));
					//add symmetric side of this as well until inferencing is hooked up
//					statements.add(factory.createStatement(relatedEntityURI, ontology.HAS_DIRECT_RELATIONSHIP_TO.URI, entityUri));
				}
				
				conn.add(statements);
			} finally {
				conn.close();
			}

		} catch (RepositoryException e) {
			log.error(e);
		}
		


		
		
		//all below code is a HACK, REMOVE ONCE TRIPLE STORE HANDLES
		EntityDescriptor desc = new InternalEntityDescriptor(entity, contentId);
		descriptorMap.put(entity.getKey(), desc);
		
		for (IndirectRelationship relationship : entity.getIndirectRelationships()) {
			ExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
			Map<String, List<EntityDescriptor>> externalIdentifierValueToEntityDescriptorMap = externalIdentifierToValueMap.get(externalIdentifier.getId());
			if (externalIdentifierValueToEntityDescriptorMap == null) {
				externalIdentifierValueToEntityDescriptorMap = new HashMap<String, List<EntityDescriptor>>();
				externalIdentifierToValueMap.put(externalIdentifier.getId(), externalIdentifierValueToEntityDescriptorMap);
			}
			List<EntityDescriptor> descriptorList = externalIdentifierValueToEntityDescriptorMap.get(externalIdentifier.getValue());
			if (descriptorList == null) {
				descriptorList = new LinkedList<EntityDescriptor>();
				externalIdentifierValueToEntityDescriptorMap.put(externalIdentifier.getValue(), descriptorList);
			}
			descriptorList.add(desc);
			if (descriptorList.size() >= 2) {
				log.info("Found '"+descriptorList.size()+"' instances of : "+externalIdentifier.getId()+" "+externalIdentifier.getValue());
			}
		}
	}
	
	
	
	/**
	 * Helper method to find an EntityURI from triple store based on given key.
	 */
	private URI findEntityURI(Key key) {
		// TODO Implement!!
		return null;
	}


	// for testing
	public void outputContents(){
		try {
			   RepositoryConnection con = repository.getConnection();
			   try {
			      String queryString = "SELECT x, p, y FROM {x} p {y}";
			      TupleQuery tupleQuery = con.prepareTupleQuery(QueryLanguage.SERQL, queryString);
			      TupleQueryResult result = tupleQuery.evaluate();
			      try {
			    	  while (result.hasNext()) {
			    		   BindingSet bindingSet = result.next();
			    		   Value subject = bindingSet.getValue("x");
			    		   Value predicate = bindingSet.getValue("p");
			    		   Value object = bindingSet.getValue("y");
			    		   log.info("triple: " + subject.stringValue() + " " + predicate.stringValue() + " " + object.stringValue());
			    		   
			    	  }
			      }
			      finally {
			         result.close();
			      }
			   }
			   finally {
			      con.close();
			   }
		} catch (RepositoryException e){
			log.error(e);
		} catch (QueryEvaluationException e2){
			log.error(e2);
		} catch (MalformedQueryException e3){
			log.error(e3);
		}
	}


	private URI genInstanceURI(String specificPart){
		return factory.createURI(BASE_URI + specificPart);
	}
	
	
	// EVERYTHING IN THIS CLASS SHOULD LIVE IN RDFS/OWL FILE EVENTUALLY
	private static class MetaDataOntology {
		private ValueFactory factory;
		private static final String BASE_MODEL_URI = "http://scap.nist.gov/cms/model#";
		
		//classes in model
		final Construct ENTITY_CLASS;
		final Construct KEY_CLASS;  //key of an entity
		final Construct FIELD_CLASS; // field of a key (to include both field id and value)
		final Construct BOUNDARY_OBJECT_CLASS; //same as 'externalId' from java model
		
		//** relationships in model **
		final Construct HAS_CONTENT_ID; //persistence ID of an entity
		final Construct HAS_KEY; // key of an entity
		final Construct HAS_KEY_TYPE; // type of key.....this should be refactored into a class heiarch???
		final Construct HAS_FIELD_DATA; // n-ary relationship between a key and the field_id/value
		final Construct HAS_FIELD_TYPE; // type (or id) of a key field
		final Construct HAS_FIELD_VALUE; // value of a field (for a specific field type).
		final Construct HAS_INDIRECT_RELATIONSHIP_TO; // Equivalent to the IndirectRelationship class...range should always be a boundary object
		final Construct HAS_DIRECT_RELATIONSHIP_TO; // entity to entity relationship
		
		MetaDataOntology(ValueFactory factory) {
			this.factory = factory;
			
			// define classes
			ENTITY_CLASS = new Construct(genModelURI("entity"), "entity");
			KEY_CLASS = new Construct(genModelURI("Key"), "Key");
			FIELD_CLASS = new Construct(genModelURI("Field"), "Field");
			BOUNDARY_OBJECT_CLASS = new Construct(genModelURI("Boundary Object"), "Boundary Object");
			
			
			// define relationships
			HAS_CONTENT_ID = new Construct(genModelURI("hasContentId"), "hasContentId");
			HAS_KEY = new Construct(genModelURI("hasKey"), "hasKey");
			HAS_KEY_TYPE = new Construct(genModelURI("hasKeyType"), "hasKeyType");
			HAS_FIELD_DATA = new Construct(genModelURI("hasFieldData"), "hasFieldData");
			HAS_FIELD_TYPE = new Construct(genModelURI("hasFieldType"), "hasFieldType");
			HAS_FIELD_VALUE = new Construct(genModelURI("hasFieldValue"), "hasFieldValue");
			HAS_INDIRECT_RELATIONSHIP_TO = new Construct(genModelURI("hasIndirectRelationshipTo"), "hasIndirectRelationshipTo");
			HAS_DIRECT_RELATIONSHIP_TO = new Construct(genModelURI("hasDirectRelationshipTo"), "hasDirectRelationshipTo");
		}
		
		void loadModel(RepositoryConnection conn) throws RepositoryException {
			// add statements
			List<Statement> statements = new LinkedList<Statement>();
			
			// assert Classes
			statements.addAll(createClass(ENTITY_CLASS.URI, ENTITY_CLASS.LABEL));
			statements.addAll(createClass(KEY_CLASS.URI, KEY_CLASS.LABEL));
			statements.addAll(createClass(FIELD_CLASS.URI, FIELD_CLASS.LABEL));
			
			// assert Predicates
			statements.addAll(createPredicate(HAS_CONTENT_ID.URI, HAS_CONTENT_ID.LABEL));
			statements.addAll(createPredicate(HAS_KEY.URI, HAS_KEY.LABEL));
			statements.addAll(createPredicate(HAS_KEY_TYPE.URI, HAS_KEY_TYPE.LABEL));
			statements.addAll(createPredicate(HAS_FIELD_DATA.URI, HAS_FIELD_DATA.LABEL));
			statements.addAll(createPredicate(HAS_FIELD_TYPE.URI, HAS_FIELD_TYPE.LABEL));
			statements.addAll(createPredicate(HAS_FIELD_VALUE.URI, HAS_FIELD_VALUE.LABEL));
			
			conn.add(statements);
		}
		
		private List<Statement> createClass(URI classUri, String label){
			List<Statement> statements = new LinkedList<Statement>();
			statements.add(factory.createStatement(classUri, RDF.TYPE, RDFS.CLASS));
			statements.add(factory.createStatement(classUri, RDFS.LABEL, factory.createLiteral(label)));
			return statements;
		}
		
		private List<Statement> createPredicate(URI predicateUri, String label){
			List<Statement> statements = new LinkedList<Statement>();
			statements.add(factory.createStatement(predicateUri, RDF.TYPE, RDF.PROPERTY));
			statements.add(factory.createStatement(predicateUri, RDFS.LABEL, factory.createLiteral("label")));
			return statements;
		}
		
		private URI genModelURI(String specificPart){
			return factory.createURI(BASE_MODEL_URI + specificPart);
		}
		
		//simple way to group a things URI and label together
		private static class Construct {
			final URI URI;
			final String LABEL;
			
			public Construct(URI uri, String label) {
				this.URI = uri;
				this.LABEL = label;
			}
		}
	}
	
	//HACK HACK HACK........REMOVE ONCE TRIPLE STORE HANDLES
	private static class InternalEntityDescriptor implements EntityDescriptor {
		private final Entity entity;
		private final String contentId;
		
		InternalEntityDescriptor(Entity entity, String contentId) {
			this.entity = entity;
			this.contentId = contentId;
		}

		@Override
		public String getContentId() {
			return contentId;
		}

		@Override
		public EntityInfo getEntityInfo() {
			return entity.getEntityInfo();
		}

		@Override
		public Key getKey() {
			return entity.getKey();
		}

		@Override
		public List<Relationship> getRelationships() {
			return entity.getRelationships();
		}

		@Override
		public List<IndirectRelationship> getIndirectRelationships() {
			return entity.getIndirectRelationships();
		}

		@Override
		public List<KeyedRelationship> getKeyedRelationships() {
			return entity.getKeyedRelationships();
		}
	}
}

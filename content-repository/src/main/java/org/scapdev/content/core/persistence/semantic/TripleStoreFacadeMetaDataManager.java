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
package org.scapdev.content.core.persistence.semantic;

import info.aduna.iteration.Iterations;

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
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
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
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.core.persistence.hybrid.MetadataStore;
import org.scapdev.content.core.persistence.semantic.translation.EntityTranslator;
import org.scapdev.content.core.persistence.semantic.translation.KeyTranslator;
import org.scapdev.content.core.persistence.semantic.translation.PartialEntityGraph;
import org.scapdev.content.core.persistence.semantic.translation.PartialEntityGraph.IncompleteStatement;
import org.scapdev.content.core.query.EntityStatistic;
import org.scapdev.content.core.query.RelationshipStatistic;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.EntityInfo;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.RelationshipInfo;

/**
 * At this point this is just going to be a facade into the triple store REST interfaces
 *
 */
public class TripleStoreFacadeMetaDataManager implements MetadataStore {
	private static final Logger log = Logger.getLogger(TripleStoreFacadeMetaDataManager.class);
	private static final String BASE_URI = "http://scap.nist.gov/cms/individuals#";

	private Repository repository;
	
	private ValueFactory factory;
	
	private MetaDataOntology ontology;
	
	private TripleStoreQueryService queryService;
	
	EntityTranslator entityTranslator;
	
	private boolean modelLoaded = false;
	
	public TripleStoreFacadeMetaDataManager() {
		//NOTE: this type is non-inferencing, see http://www.openrdf.org/doc/sesame2/2.3.2/users/ch08.html for more detail
		try {
			repository = new SailRepository(new MemoryStore());
			
//			repository = new HTTPRepository("http://localhost:8080/openrdf-sesame", "scapCmsTest");
			
			
			repository.initialize();
			factory = repository.getValueFactory();
			ontology = new MetaDataOntology(factory);
			queryService = new TripleStoreQueryService(repository, ontology);
			entityTranslator = new EntityTranslator(
					BASE_URI, ontology, factory);
		} catch (RepositoryException e){
			log.error("Exception iniitalizing triple store", e);
		}
		
	}
	
	@Override
	public Entity getEntity(Key key, ContentRetrieverFactory contentRetrieverFactory, MetadataModel model) {
		try {
			RepositoryConnection conn = repository.getConnection();
			try {
				URI entityURI = queryService.findEntityURI(key, conn);
				if (entityURI != null){
					Set<Statement> entityStatements = getEntityStatements(entityURI, conn);
					// need to find the entityKeys from the KeyedRelationship...these are not included in owningEntityContext on persist
					Map<URI, Key> relatedEntityKeys = findEntityKeys(queryService.findAllRelatedEntityURIs(entityURI, conn), conn);
					return entityTranslator.translateToJava(entityStatements, relatedEntityKeys,
							model, contentRetrieverFactory);
				}
			} catch (MalformedQueryException e){
				log.error(e);
				throw new RuntimeException(e);
			} catch (QueryEvaluationException e2) {
				log.error(e2);
				throw new RuntimeException(e2);
			} finally {
				conn.close();	
			}

		} catch (RepositoryException e) {
			log.error(e);
		}
		return null;
	}
	

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType,
			Collection<String> indirectIds, Set<String> entityType) {
		try {
			RepositoryConnection conn = repository.getConnection();
			try {
				List<URI> entityURIs = queryService
						.findEntityUrisFromBoundaryObjectIds(indirectType,
								indirectIds, entityType, conn);
				return new HashSet<Key>(findEntityKeys(entityURIs, conn)
						.values());

			} catch (MalformedQueryException e) {
				log.error(e);
				throw new RuntimeException(e);
			} catch (QueryEvaluationException e2) {
				log.error(e2);
				throw new RuntimeException(e2);
			} finally {
				conn.close();
			}

		} catch (RepositoryException e) {
			log.error(e);
		}
		return null;
	}
	
	@Override
	public Map<String, ? extends EntityStatistic> getEntityStatistics(
			Set<String> entityInfoIds, MetadataModel model) {
		Map<String, InternalEntityStatistic> entityStats = new HashMap<String, InternalEntityStatistic>();
		try {
			RepositoryConnection conn = repository.getConnection();
			try {
				for (String entityTypeId : entityInfoIds) {
					List<URI> existingEntities = findEntitybyEntityTypeId(entityTypeId, conn);
					InternalEntityStatistic stat = new InternalEntityStatistic(model.getEntityInfoById(entityTypeId));
					// 4. loop through this list, incrementing count of entityStatistic for every iteration
					for (URI entityURI : existingEntities){
						stat.incrementCount();
						List<String> relationshipIds = findAllRelationshipsFromEntity(entityURI, conn);
						for (String relationshipId : relationshipIds){
							stat.handleRelationships(relationshipId, model.getRelationshipInfoById(relationshipId));
						}
					}
					entityStats.put(entityTypeId, stat);
				}
				
			} finally {
				conn.close();
			}
		} catch (RepositoryException e) {
			log.error(e);
		}
		return entityStats;
	}
	
	@Override
	public void persist(Map<String, Entity> contentIdToEntityMap, MetadataModel model) {
		try {
			RepositoryConnection conn = repository.getConnection();
			try {
				if (!modelLoaded){
					ontology.loadModel(conn, model);
					modelLoaded = true;
				}
				Map<Key, String> keyToContentIdMap = new HashMap<Key, String>(); //need reverse map for resolving direct dependencies at cleanup
				Map<BNode, List<IncompleteStatement>> incompleteStatements = new HashMap<BNode, List<IncompleteStatement>>();
				for (Map.Entry<String, Entity> entry : contentIdToEntityMap.entrySet()){
					String contentId = entry.getKey();
					Entity entity = entry.getValue();
					keyToContentIdMap.put(entity.getKey(), contentId);  
					
					BNode context = factory.createBNode();
					PartialEntityGraph entityGraph = entityTranslator.translateToRdf(entity, contentId);
					incompleteStatements.put(context, entityGraph.getIncompleteStatements());
					conn.add(entityGraph.getCompleteStatements(), context);
				}
				//resolve incomplete statements
				persistIncompleteStatements(incompleteStatements, keyToContentIdMap, conn);
			} finally {
				conn.close();
			}
		} catch (RepositoryException e) {
			log.error(e);
		}
	}
	
	/**
	 * Helper method to clean up after the incomplete statements that connect the direct relationships between entities
	 * @param incompleteStatements
	 * @param keyToContentIdMap
	 * @param conn
	 * @throws RepositoryException 
	 */
	private void persistIncompleteStatements(
			Map<BNode, List<IncompleteStatement>> incompleteStatements,
			Map<Key, String> keyToContentIdMap, RepositoryConnection conn) throws RepositoryException{
		Map<Key, URI> keyToEntityURICache = new HashMap<Key, URI>(); 
		for (Map.Entry<BNode, List<IncompleteStatement>> entry : incompleteStatements.entrySet()) {
			BNode context = entry.getKey();
			for (IncompleteStatement incompleteStatement : entry.getValue()) {
				Key relatedEntityKey = incompleteStatement.getRelatedEntityKey();
				String relatedEntityContentId = keyToContentIdMap.get(relatedEntityKey);
				URI relatedEntityURI = keyToEntityURICache.get(relatedEntityKey);
				if (relatedEntityURI == null){
					relatedEntityURI = findEntityURI(relatedEntityKey, relatedEntityContentId, conn);
					keyToEntityURICache.put(relatedEntityKey, relatedEntityURI);
				}
				if (relatedEntityURI != null) {
					conn.add(factory.createStatement(
							incompleteStatement.getSubject(),
							incompleteStatement.getPredicate(),
							relatedEntityURI), context);
				}
			}
		}
	}
	
	/**
	 * Helper method to generate keys for all entities 
	 * @param entityURIs
	 * @param conn
	 * @return
	 * @throws MalformedQueryException 
	 * @throws QueryEvaluationException 
	 * @throws RepositoryException 
	 */
	private Map<URI, Key> findEntityKeys(List<URI> entityURIs, RepositoryConnection conn) throws RepositoryException, QueryEvaluationException, MalformedQueryException{
		KeyTranslator keyTranslator = new KeyTranslator(BASE_URI, ontology, factory);
		Map<URI, Key> entityURIToKeyMap= new HashMap<URI, Key>();
		for (URI entityURI : entityURIs){
			Set<Statement> entityStatements = getEntityStatements(entityURI, conn);
			//TODO: may want to refactor to only pass translator key statements, but this will work
			Key entityKey = keyTranslator.translateToJava(entityStatements);
			entityURIToKeyMap.put(entityURI, entityKey);
		}
		return entityURIToKeyMap;
	}
	
	/**
	 * Uses context to get all triples associated with entityURI
	 * @param entityUri
	 * @return
	 * @throws RepositoryException 
	 * @throws MalformedQueryException 
	 * @throws QueryEvaluationException 
	 */
	private Set<Statement> getEntityStatements(URI entityURI, RepositoryConnection conn) throws RepositoryException, QueryEvaluationException, MalformedQueryException{
		Resource entityContextURI = queryService.findEntityContext(entityURI, conn);
		//no need to run inferencing here
		return Iterations.addAll(conn
				.getStatements(null, null, null, false,
						entityContextURI),
				new HashSet<Statement>());
	}


	/**
	 * Helper method to find entityURI by contentID. If it cannot find it by
	 * contentId it will search by Key.
	 * 
	 * @param key
	 * @param value
	 * @param conn
	 * @return null if nothing is found
	 */
	private URI findEntityURI(Key key, String contentId, RepositoryConnection conn) {
		try {
			URI entityURI = null;
			if (contentId != null){
				 entityURI = queryService.findEntityURIbyContentId(contentId, conn);
			}
			if (entityURI == null){
				entityURI = queryService.findEntityURI(key, conn);
			}
			return entityURI;
		} catch (QueryEvaluationException e) {
			log.error(e);
		} catch (RepositoryException e) {
			log.error(e);
		} catch (MalformedQueryException e) {
			log.error(e);
		}
		return null;
	}
	
	/**
	 * Find all entities of a given type
	 * @param entityTypeId - note: this is the same as the EntityInfo.getId();
	 * @return
	 */
	private List<URI> findEntitybyEntityTypeId(String entityTypeId, RepositoryConnection conn){
		List<URI> entityURIs = new LinkedList<URI>();
		try {
			if (entityTypeId != null && !entityTypeId.isEmpty()){
				entityURIs.addAll(queryService.findEntityURIsByEntityType(entityTypeId, conn));
			}
		} catch (QueryEvaluationException e) {
			log.error(e);
		} catch (RepositoryException e) {
			log.error(e);
		} catch (MalformedQueryException e) {
			log.error(e);
		}
		return entityURIs;
	}

	/**
	 * Find all relationships (i.e. anything that is a subProperty of
	 * HAS_DIRECT_RELATIONSHIP or HAS_INDIRECT_RELATIONSHIP) associated with the
	 * specific entity. NOTE: does not include the higher level relationships in
	 * the returned list of relationship IDs.
	 * 
	 * @param entityTypeId
	 *            - note: this is the same as the EntityInfo.getId();
	 * @return
	 */
	private List<String> findAllRelationshipsFromEntity(URI entityUri, RepositoryConnection conn){
		List<String> relationshipIds = new LinkedList<String>();
		try {
			if (entityUri != null){
				relationshipIds.addAll(queryService.findAllRelationshipsFromEntity(entityUri, conn));
			}
		} catch (QueryEvaluationException e) {
			log.error(e);
		} catch (RepositoryException e) {
			log.error(e);
		} catch (MalformedQueryException e) {
			log.error(e);
		}
		return relationshipIds;
	}

	/**
	 * For testing only
	 */
	@SuppressWarnings("unused")
	private void outputContents(){
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
			    		   log.trace("triple: " + subject.stringValue() + " " + predicate.stringValue() + " " + object.stringValue());
			    		   
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


	
	private class InternalEntityStatistic implements EntityStatistic {
		private final EntityInfo entityInfo;
		private int count = 0;
		private final Map<String, InternalRelationshipStatistic> relationshipStats = new HashMap<String, InternalRelationshipStatistic>();

		public InternalEntityStatistic(EntityInfo entityInfo) {
			this.entityInfo = entityInfo;
		}

		public void handleRelationships(String relationshipId, RelationshipInfo info) {
			InternalRelationshipStatistic stat = relationshipStats.get(relationshipId);
			if (stat == null) {
				stat = new InternalRelationshipStatistic(info);
				relationshipStats.put(relationshipId, stat);
			}
			stat.incrementCount();
		}

		public void incrementCount() {
			++count;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public EntityInfo getEntityInfo() {
			return entityInfo;
		}

		@Override
		public Map<String, ? extends RelationshipStatistic> getRelationshipInfoStatistics() {
			return Collections.unmodifiableMap(relationshipStats);
		}
		
	}

	private class InternalRelationshipStatistic implements RelationshipStatistic {
		private final RelationshipInfo relationshipInfo;
		private int count = 0;

		public InternalRelationshipStatistic(RelationshipInfo relationshipInfo) {
			this.relationshipInfo = relationshipInfo;
		}

		public void incrementCount() {
			++count;
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public RelationshipInfo getRelationshipInfo() {
			return relationshipInfo;
		}
	}
}

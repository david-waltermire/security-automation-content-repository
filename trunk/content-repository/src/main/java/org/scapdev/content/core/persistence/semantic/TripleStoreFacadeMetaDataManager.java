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
import org.scapdev.content.core.persistence.semantic.translation.PartialEntityGraph;
import org.scapdev.content.core.persistence.semantic.translation.PartialEntityGraph.IncompleteStatement;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.ExternalIdentifier;
import org.scapdev.content.model.ExternalIdentifierInfo;
import org.scapdev.content.model.IndirectRelationship;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.MetadataModel;

/**
 * At this point this is just going to be a facade into the triple store REST interfaces
 *
 */
public class TripleStoreFacadeMetaDataManager implements MetadataStore {
	private static final Logger log = Logger.getLogger(TripleStoreFacadeMetaDataManager.class);
	private static final String BASE_URI = "http://scap.nist.gov/cms/individuals#";

	//HACK, REMOVE ONCE TRIPLE STORE HANDLES
	private final Map<Key, Entity> descriptorMap = new HashMap<Key, Entity>();
	private final Map<String, Map<String, List<Entity>>> externalIdentifierToValueMap = new HashMap<String, Map<String, List<Entity>>>();;

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
//				if (entityURI != null){
//					Resource entityContextURI = queryService.findEntityContext(entityURI, conn);
//					//no need to run inferencing here
//					return entityTranslator.translateToJava(
//							Iterations.addAll(conn.getStatements(null, null, null,
//									false, entityContextURI),
//									new LinkedList<Statement>()), model,
//							contentRetrieverFactory);
//				}
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
		return descriptorMap.get(key);
	}
	
	@Override
	public List<Entity> getEntity(ExternalIdentifierInfo externalIdentifierInfo, String value, ContentRetrieverFactory contentRetrieverFactory, MetadataModel model) {
		//HACK, REMOVE ONCE TRIPLE STORE HANDLES
		Map<String, List<Entity>> externalIdentifierValueToEntityMap = externalIdentifierToValueMap.get(externalIdentifierInfo.getId());
		List<Entity> result;
		if (externalIdentifierValueToEntityMap.containsKey(value)) {
			result = Collections.unmodifiableList(externalIdentifierValueToEntityMap.get(value));
		} else {
			result = Collections.emptyList();
		}
		return result;
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType, Collection<String> indirectIds, Set<String> entityType) {
		//HACK, REMOVE ONCE TRIPLE STORE HANDLES
		Set<Key> result = Collections.emptySet();
		Map<String, List<Entity>> indirectValueToDescriptorsMap = externalIdentifierToValueMap.get(indirectType);
		if (indirectValueToDescriptorsMap != null) {
			result = new HashSet<Key>();
			for (String indirectId : indirectIds) {
				List<Entity> descs = indirectValueToDescriptorsMap.get(indirectId);
				if (descs != null) {
					for (Entity desc : descs) {
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
		
		//all below code is a HACK, REMOVE ONCE TRIPLE STORE HANDLES
		for (Map.Entry<String, Entity> entry : contentIdToEntityMap.entrySet()){
			Entity entity = entry.getValue();
			descriptorMap.put(entity.getKey(), entity);
		
			for (IndirectRelationship relationship : entity.getIndirectRelationships()) {
	
					ExternalIdentifier externalIdentifier = relationship.getExternalIdentifier();
					Map<String, List<Entity>> externalIdentifierValueToEntityMap = externalIdentifierToValueMap.get(externalIdentifier.getId());
					if (externalIdentifierValueToEntityMap == null) {
						externalIdentifierValueToEntityMap = new HashMap<String, List<Entity>>();
						externalIdentifierToValueMap.put(externalIdentifier.getId(), externalIdentifierValueToEntityMap);
					}
					List<Entity> descriptorList = externalIdentifierValueToEntityMap.get(externalIdentifier.getValue());
					if (descriptorList == null) {
						descriptorList = new LinkedList<Entity>();
						externalIdentifierValueToEntityMap.put(externalIdentifier.getValue(), descriptorList);
					}
					descriptorList.add(entity);
					if (descriptorList.size() >= 2) {
						log.trace("Found '"+descriptorList.size()+"' instances of : "+externalIdentifier.getId()+" "+externalIdentifier.getValue());
					}
				}
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
		for (Map.Entry<BNode, List<IncompleteStatement>> entry : incompleteStatements.entrySet()) {
			BNode context = entry.getKey();
			for (IncompleteStatement incompleteStatement : entry.getValue()) {
				Key relatedEntityKey = incompleteStatement.getRelatedEntityKey();
				String relatedEntityContentId = keyToContentIdMap.get(relatedEntityKey);
				URI relatedEntityURI = findEntityURI(relatedEntityKey, relatedEntityContentId, conn);
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

	// for testing
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


}

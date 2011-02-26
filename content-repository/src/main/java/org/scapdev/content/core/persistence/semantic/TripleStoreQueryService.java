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

import java.util.Map;

import org.openrdf.model.Resource;
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
import org.scapdev.content.model.Key;

/**
 * A service class to provide any triple store searching related services
 *
 */
public class TripleStoreQueryService {
	private static final String NEW_LINE = System.getProperty("line.separator");
	
	private Repository repository;
	
	private ValueFactory factory;
	
	private MetaDataOntology ontology;
	
	public TripleStoreQueryService(Repository repository, MetaDataOntology ontology) {
		this.repository = repository;
		this.factory = repository.getValueFactory();
		this.ontology = ontology;
	}
	
	/**
	 * Helper method to find an EntityURI from triple store based on given key.
	 * @param key - key to search on
	 * @param conn - conn to use to execute query
	 * @return URI of entity associated with key, or null if no entity is found
	 * @throws QueryEvaluationException 
	 * @throws MalformedQueryException 
	 * @throws RepositoryException 
	 */
	URI findEntityURI(Key key, RepositoryConnection conn) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		// TODO Implement!!
		String entityURIVariableName = "_e";
		TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SERQL, generateEntitySearchQuery(key, entityURIVariableName));
	    TupleQueryResult result = tupleQuery.evaluate();
	    BindingSet resultSet = null;
	    int resultSize = 0;
	    while (result.hasNext()){
	    	if (resultSize > 1){
	    		throw new NonUniqueResultException();
	    	}
	    	resultSet = result.next(); 
	    	resultSize++;
	    }
	    if (resultSet != null){
	    	Value entityURI = resultSet.getValue(entityURIVariableName);
	    	return factory.createURI(entityURI.stringValue());
	    }
	    return null;
	}
	
	/**
	 * Helper method that will generate String query searching for entity defined by given Key
	 * @param key
	 * @param entityURIVariableName - name to use for entity variable
	 * @return - the query that will produce the entity URI
	 *	TODO: change to sparql query?
	 */
	private String generateEntitySearchQuery(Key key, String entityURIVariableName){
		StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT ").append(entityURIVariableName).append(" ").append(NEW_LINE);
		//_e hasKey _key
		queryBuilder.append("FROM {_e} ");
		queryBuilder.append("<").append(ontology.HAS_KEY.URI).append(">");
		queryBuilder.append(" {_key},").append(NEW_LINE);
		//_key hasKeyType _keyType
		queryBuilder.append("{_key} ");
		queryBuilder.append("<").append(ontology.HAS_KEY_TYPE.URI).append(">");
		queryBuilder.append(" {\"").append(key.getId()).append("\"},").append(NEW_LINE);
		int fieldNumber = 1;
		for (Map.Entry<String, String> keyFieldEntry : key.getIdToValueMap().entrySet()){
			if (fieldNumber > 1){
				queryBuilder.append(",").append(NEW_LINE);
			}
			//_key hasField _fieldX
			String fieldNameDeclaration = "{_field" + fieldNumber + "}";
			queryBuilder.append("{_key} ");
			queryBuilder.append("<").append(ontology.HAS_FIELD_DATA.URI).append(">");
			queryBuilder.append(" ").append(fieldNameDeclaration).append(",").append(NEW_LINE);
			//_fieldx hasFieldType type
			queryBuilder.append(fieldNameDeclaration);
			queryBuilder.append("<").append(ontology.HAS_FIELD_TYPE.URI).append(">");
			queryBuilder.append(" {\"").append(keyFieldEntry.getKey()).append("\"},").append(NEW_LINE);
			//_fieldx hasFieldValue value
			queryBuilder.append(fieldNameDeclaration);
			queryBuilder.append("<").append(ontology.HAS_FIELD_VALUE.URI).append(">");
			queryBuilder.append(" {\"").append(keyFieldEntry.getValue()).append("\"}");
			fieldNumber++;
		}
		return queryBuilder.toString();
	}
	
	
	/**
	 * Helper method to find the context associated with the triples necessary to re-constitute an entity
	 * @param entityURI
	 * @param conn
	 * @return
	 */
	protected Resource findEntityContext(URI entityURI, RepositoryConnection conn) throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		StringBuilder queryBuilder = new StringBuilder();
		String sourceVariableName = "source";
		queryBuilder.append("SELECT source FROM CONTEXT ").append(sourceVariableName).append(" {<");
		queryBuilder.append(entityURI).append(">} ");
		queryBuilder.append("<").append(ontology.HAS_KEY.URI).append("> ");
		queryBuilder.append("{_key}");
		TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SERQL, queryBuilder.toString());
	    TupleQueryResult result = tupleQuery.evaluate();
	    BindingSet resultSet = null;
	    int resultSize = 0;
	    while (result.hasNext()){
	    	if (resultSize > 1){
	    		throw new NonUniqueResultException();
	    	}
	    	resultSet = result.next(); 
	    	resultSize++;
	    }
	    if (resultSet != null){
	    	Value entityContextURI = resultSet.getValue(sourceVariableName);
	    	return factory.createBNode(entityContextURI.stringValue());
	    }
	    return null;
	    
	}
	
	static class NonUniqueResultException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public NonUniqueResultException() {
			super();
		}
	}
	
}

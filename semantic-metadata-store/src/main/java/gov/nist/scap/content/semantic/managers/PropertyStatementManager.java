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
package gov.nist.scap.content.semantic.managers;

import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IPropertyRef;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.entity.EntityBuilder;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Used to process statements to reconstruct a version
 * @author Adam Halbardier
 *
 */
public class PropertyStatementManager implements RegenerationStatementManager {
    private final IPersistenceContext ipc;
    private final MetaDataOntology ontology;
    private final URI owningEntityURI;
    
    private String entityType = null;

	
    /**
     * the default constructor
     * @param ipc the persistence context
     */
    public PropertyStatementManager(IPersistenceContext ipc, URI owningEntityURI) {
	    this.ipc = ipc;
	    this.owningEntityURI = owningEntityURI;
	    this.ontology = ipc.getOntology();
	}
	
	@Override
	public void populateEntity(EntityBuilder builder) throws RepositoryException {
	    RepositoryConnection conn = null;
	    try {
    	    StringBuilder queryBuilder = new StringBuilder();
            String entityTypeVar = "_entityType";
            String propertyName = "_propertyName";
            String propertyValue = "_propertyValue";
            queryBuilder.append("SELECT DISTINCT ").append(entityTypeVar).append(", ").append(propertyName).append(", ").append(propertyValue).append(" FROM ").append("\n");
            queryBuilder.append("{<").append(owningEntityURI).append(">} ").append("<").append(ontology.HAS_ENTITY_TYPE.URI).append(">").append(" {").append(entityTypeVar).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_PROPERTY.URI).append(">").append(" {_versionBNode},").append("\n");
            queryBuilder.append("{_versionBNode}").append("<").append(ontology.HAS_PROPERTY_NAME.URI).append(">").append(" {").append(propertyName).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_PROPERTY_VALUE.URI).append(">").append(" {").append(propertyValue).append("}").append("\n");
            conn = ipc.getRepository().getConnection();
            TupleQuery tupleQuery =
                conn.prepareTupleQuery(
                    QueryLanguage.SERQL,
                    queryBuilder.toString());
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet resultSet = null;
            Map<String,Set<String>> results = new HashMap<String, Set<String>>();
            
            String propertyNameRes;
            String propertyValueRes;
            while (result.hasNext()) {
                resultSet = result.next();
                if( entityType == null ) {
                    entityType = resultSet.getValue(entityTypeVar).stringValue();
                } else if( !entityType.equals(resultSet.getValue(entityTypeVar).stringValue()) ){
                    throw new RepositoryException("Found 2 entity types for the same entity. Entity " + owningEntityURI.stringValue());
                }
                
                propertyNameRes = resultSet.getValue(propertyName).stringValue();
                propertyValueRes = resultSet.getValue(propertyValue).stringValue();
                if( !results.containsKey(propertyNameRes) ) {
                	results.put(propertyNameRes, new HashSet<String>());
                }
                results.get(propertyNameRes).add(propertyValueRes);
            }
            
            if( entityType == null ) 
                throw new RepositoryException("Could not find entity type. Entity " + owningEntityURI.stringValue());
            
            IEntityDefinition eDef = ontology.getEntityDefinitionById(entityType);
            if( eDef.getPropertyRefs() != null ) {
            	for( IPropertyRef ipr : eDef.getPropertyRefs() ) {
            		Set<String> propVals = results.get(ipr.getPropertyDefinition().getId());
            		if( propVals != null ) {
            			for( String propVal : propVals ) {
            				builder.addProperty(ipr.getPropertyDefinition(), propVal);
            			}
            		}
            	}
            }
            
            
        } catch (MalformedQueryException e) {
            throw new RepositoryException(e);
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        } finally {
            if( conn != null )
                conn.close();
        }


	}
}

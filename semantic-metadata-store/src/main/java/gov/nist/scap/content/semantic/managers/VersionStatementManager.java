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

import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.entity.EntityBuilder;

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
public class VersionStatementManager implements RegenerationStatementManager {
    private final IPersistenceContext ipc;
    private final MetaDataOntology ontology;
    private final URI owningEntityURI;
	
    /**
     * the default constructor
     * @param ipc the persistence context
     */
    public VersionStatementManager(IPersistenceContext ipc, URI owningEntityURI) {
	    this.ipc = ipc;
	    this.owningEntityURI = owningEntityURI;
	    this.ontology = ipc.getOntology();
	}
	
	@Override
	public void populateEntity(EntityBuilder builder) throws RepositoryException {
        //TODO does version type need to be read from the triple store?

	    RepositoryConnection conn = null;
	    try {
    	    StringBuilder queryBuilder = new StringBuilder();
            String versionType = "_versionType";
            String versionValue = "_versionValue";
            queryBuilder.append("SELECT DISTINCT ").append(versionType).append(", ").append(versionValue).append(" FROM ").append("\n");
            queryBuilder.append("{<").append(owningEntityURI).append(">} ").append("<").append(ontology.HAS_VERSION.URI).append(">").append(" {_versionBNode},").append("\n");
            queryBuilder.append("{_versionBNode}").append("<").append(ontology.HAS_VERSION_TYPE.URI).append(">").append(" {").append(versionType).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_VERSION_VALUE.URI).append(">").append(" {").append(versionValue).append("}").append("\n");
            conn = ipc.getRepository().getConnection();
            TupleQuery tupleQuery =
                conn.prepareTupleQuery(
                    QueryLanguage.SERQL,
                    queryBuilder.toString());
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet resultSet = null;
            while (result.hasNext()) {
                if( resultSet != null ) {
                    throw new RepositoryException("Entity has more than 1 version! Entity: " + owningEntityURI);
                }
                resultSet = result.next();
                builder.setVersionValue(resultSet.getValue(versionValue).stringValue());
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

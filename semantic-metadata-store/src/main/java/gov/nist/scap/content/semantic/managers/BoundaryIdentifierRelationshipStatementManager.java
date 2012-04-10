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

import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.builders.BoundaryIdentifierRelationshipBuilder;
import gov.nist.scap.content.semantic.entity.EntityBuilder;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

/**
 * <p>A manager to coordinate multiple BoundaryIdentiferRelationshipBuilders working in
 * parallel to build up all BoundaryIdentiferRelationships originating from a specific
 * entity.</p>
 * 
 * @see BoundaryIdentiferRelationshipBuilder
 */
public class BoundaryIdentifierRelationshipStatementManager implements RegenerationStatementManager {
	private MetaDataOntology ontology;
	
	private URI owningEntityURI;
	
	private IPersistenceContext ipc;
	

	/**
	 * the default constructor
	 * @param ipc the persistence context
	 * @param owningEntityURI the entity being built
	 */
	public BoundaryIdentifierRelationshipStatementManager(IPersistenceContext ipc, URI owningEntityURI) {
		this.ipc = ipc;
	    this.ontology = ipc.getOntology();
		this.owningEntityURI = owningEntityURI;
	}
	
	@Override
	public void populateEntity(EntityBuilder builder) throws RepositoryException {
        try {
            StringBuilder queryBuilder = new StringBuilder();
            String relType = "_relType";
            String bType = "_bType";
            String bValue = "_bValue";
            queryBuilder.append("SELECT DISTINCT ").append(relType).append(", ").append(bType).append(", ").append(bValue).append(" FROM ").append("\n");
            queryBuilder.append("{<").append(owningEntityURI).append(">} ").append(relType).append(" {_boundaryIdURI},").append("\n");
            queryBuilder.append("{").append(relType).append("} ").append("<").append(RDFS.SUBPROPERTYOF).append(">").append(" {<").append(ontology.HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.URI).append(">},").append("\n");
            queryBuilder.append("{_boundaryIdURI} ").append("<").append(ontology.HAS_BOUNDARY_OBJECT_TYPE.URI).append(">").append(" {").append(bType).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_BOUNDARY_OBJECT_VALUE.URI).append(">").append(" {").append(bValue).append("}").append("\n");
            TupleQuery tupleQuery =
                ipc.getRepository().getConnection().prepareTupleQuery(
                    QueryLanguage.SERQL,
                    queryBuilder.toString());
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet resultSet = null;
            while (result.hasNext()) {
                resultSet = result.next();
                // the reasoner treats this as a subproperty of itself
                if( !ontology.HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.URI.stringValue().equals(resultSet.getValue(relType).stringValue()) ) {
                    BoundaryIdentifierRelationshipBuilder birb = new BoundaryIdentifierRelationshipBuilder();
                    birb.setBoundaryIdentiferRelationshipInfo((IBoundaryIdentifierRelationshipDefinition)ontology.getRelationshipDefinitionById(resultSet.getValue(relType).stringValue()));
                    birb.setExternalIdType(resultSet.getValue(bType).stringValue());
                    birb.setExternalIdValue(resultSet.getValue(bValue).stringValue());
                    builder.addRelationship(birb.build(ontology));
                }
            }
        } catch (MalformedQueryException e) {
            throw new RepositoryException(e);
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }
	}
}

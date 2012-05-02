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

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.builders.CompositeRelationshipBuilder;
import gov.nist.scap.content.semantic.entity.EntityBuilder;
import gov.nist.scap.content.semantic.entity.EntityProxy;
import gov.nist.scap.content.semantic.entity.KeyedEntityProxy;

import org.openrdf.model.URI;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * <p>A manager to build up composite relationships</p>
 * 
 * @see CompositeRelationshipBuilder
 */
public class CompositeRelationshipStatementManager implements RegenerationStatementManager {
    private MetaDataOntology ontology;
    
    private IPersistenceContext ipc;
    
    private URI owningEntityURI;

    /**
     * The default constructor
     * @param ipc the persistence context
     * @param owningEntityURI the owning entity of the relationship
     */
    public CompositeRelationshipStatementManager(IPersistenceContext ipc, URI owningEntityURI) {
        this.ontology = ipc.getOntology();
        this.ipc = ipc;
        this.owningEntityURI = owningEntityURI;
    }
	
	@Override
	public void populateEntity(EntityBuilder builder) throws RepositoryException {
	    RepositoryConnection conn = null;
        try {
            StringBuilder queryBuilder = new StringBuilder();
            String relType = "_relType";
            String relatedEntity = "_relatedEntityURI";
            queryBuilder.append("SELECT DISTINCT ").append(relType).append(", ").append(relatedEntity).append(" FROM ").append("\n");
            queryBuilder.append("{<").append(owningEntityURI).append(">} ").append(relType).append(" {").append(relatedEntity).append("},").append("\n");
            queryBuilder.append("{").append(relType).append("} ").append("<").append(RDFS.SUBPROPERTYOF).append(">").append(" {<").append(ontology.HAS_COMPOSITE_RELATIONSHIP_TO.URI).append(">}").append("\n");
            conn = ipc.getRepository().getConnection();
            TupleQuery tupleQuery =
                conn.prepareTupleQuery(
                    QueryLanguage.SERQL,
                    queryBuilder.toString());
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet resultSet = null;
            while (result.hasNext()) {
                resultSet = result.next();
                // the reasoner treats this as a subproperty of itself
                if( !ontology.HAS_COMPOSITE_RELATIONSHIP_TO.URI.stringValue().equals(resultSet.getValue(relType).stringValue()) ) {
                    CompositeRelationshipBuilder crb = new CompositeRelationshipBuilder();
                    crb.setCompositeRelationshipInfo((ICompositeRelationshipDefinition)ontology.getRelationshipDefinitionById(resultSet.getValue(relType).stringValue()));
                    crb.setRelatedEntity(new EntityProxy<IEntityDefinition,IEntity<IEntityDefinition>>(ipc,(URI)resultSet.getValue(relatedEntity)));
                    builder.addRelationship(crb.build(ontology));
                }
            }
            
            queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT DISTINCT ").append(relatedEntity).append(" FROM ").append("\n");
            queryBuilder.append("{<").append(owningEntityURI).append(">} ").append("<").append(ontology.HAS_PARENT_RELATIONSHIP_TO.URI).append(">").append(" {").append(relatedEntity).append("}").append("\n");
            tupleQuery =
                conn.prepareTupleQuery(
                    QueryLanguage.SERQL,
                    queryBuilder.toString());
            result = tupleQuery.evaluate();
            resultSet = null;
            while (result.hasNext()) {
                if( resultSet != null ) {
                    throw new RepositoryException("Entity has more than 1 parent! Entity: " + owningEntityURI);
                }
                resultSet = result.next();
                builder.setParent(new KeyedEntityProxy<IKeyedEntityDefinition, IKeyedEntity<IKeyedEntityDefinition>>(ipc, (URI)resultSet.getValue(relatedEntity)));
            }

        } catch (MalformedQueryException e) {
            throw new RepositoryException(e);
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        } finally {
            if( conn != null ) {
                conn.close();
            }
        }
	}
	
}

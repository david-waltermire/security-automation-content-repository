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
package gov.nist.scap.content.semantic.translation;

import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.entity.EntityBuilder;
import gov.nist.scap.content.semantic.managers.BoundaryIdentifierRelationshipStatementManager;
import gov.nist.scap.content.semantic.managers.CompositeRelationshipStatementManager;
import gov.nist.scap.content.semantic.managers.KeyStatementManager;
import gov.nist.scap.content.semantic.managers.KeyedRelationshipStatementManager;
import gov.nist.scap.content.semantic.managers.RegenerationStatementManager;
import gov.nist.scap.content.semantic.managers.VersionStatementManager;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;

/**
 * Translates entities across the different modeling languages.
 */
public class EntityTranslator {
    private final IPersistenceContext persistContext;
    private final MetaDataOntology ontology;


    /**
     * default constructor
     * @param persistContext The persistence context
     */
    public EntityTranslator(IPersistenceContext persistContext) {
        this.persistContext = persistContext;
        this.ontology = persistContext.getOntology();
    }

    /**
     * @param <T> some extension of IEntity
     * @return the constructed entity
     */
    public <T extends IEntity<?>> T translateToJava(URI entityURI, ContentRetrieverFactory crf) throws RepositoryException {
        List<RegenerationStatementManager> managers =
            new LinkedList<RegenerationStatementManager>();
        managers.add(new BoundaryIdentifierRelationshipStatementManager(
            persistContext, entityURI));
        managers.add(new KeyedRelationshipStatementManager(
            persistContext,
            entityURI));
        managers.add(new CompositeRelationshipStatementManager(persistContext, entityURI));
        managers.add(new KeyStatementManager(persistContext, entityURI));
        managers.add(new VersionStatementManager(persistContext, entityURI));

        EntityBuilder builder = new EntityBuilder();
        try {

            StringBuilder queryBuilder = new StringBuilder();
            String entityType = "_entityType";
            String contentId = "_contentId";
            queryBuilder.append("SELECT DISTINCT ").append(entityType).append(", ").append(contentId).append(" FROM ").append("\n");
            queryBuilder.append("{<").append(entityURI).append(">} <").append(ontology.HAS_ENTITY_TYPE.URI).append("> {").append(entityType).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_CONTENT_ID.URI).append("> {").append(contentId).append("}").append("\n");
            TupleQuery tupleQuery =
                persistContext.getRepository().getConnection().prepareTupleQuery(
                    QueryLanguage.SERQL,
                    queryBuilder.toString());
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet resultSet = null;
            while (result.hasNext()) {
                if( resultSet != null ) {
                    throw new RepositoryException("Entity has more than 1 content id or entity type! Entity " + entityURI.stringValue());
                }
                resultSet = result.next();
                builder.setContentRetriever((crf.newContentRetriever(resultSet.getValue(contentId).stringValue())));
                builder.setEntityDefinition(ontology.getEntityDefinitionById(resultSet.getValue(entityType).stringValue()));
            }
        } catch (MalformedQueryException e) {
            throw new RepositoryException(e);
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }

        for (RegenerationStatementManager statementManager : managers) {
            statementManager.populateEntity(builder);
        }

        return builder.build();
    }
}

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
package gov.nist.scap.content.semantic;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IVersion;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.semantic.exceptions.NonUniqueResultException;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A service class to provide any triple store searching related services
 */
public class TripleStoreQueryService {
    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final Logger log =
        LoggerFactory.getLogger(TripleStoreQueryService.class);

    private ValueFactory factory;

    private MetaDataOntology ontology;

    private IPersistenceContext ipc;

    /**
     * @param persistContext the context for the triple store
     * @throws RepositoryException error while getting connection to repository
     */
    public TripleStoreQueryService(IPersistenceContext persistContext)
            throws RepositoryException {
        this.factory = persistContext.getRepository().getValueFactory();
        this.ontology = persistContext.getOntology();
        this.ipc = persistContext;
    }

    /**
     * method to find a EntityURIs from triple store based on given key (all versions).
     * 
     * @param key - key to search on
     * @return Set<URI> of entities associated with key, or null if no entity is found
     * @throws RepositoryException error while executing query
     */
    public Set<URI> findEntityURIs(IKey key) throws RepositoryException {
        return findEntityURIs(key, null);
    }
    
    /**
     * method to find a EntityURIs from triple store based on given key (all versions).
     * 
     * @param key - key to search on
     * @param version version of the entity to return
     * @return Set<URI> of entities associated with key, or null if no entity is found
     * @throws RepositoryException error while executing query
     */
    public Set<URI> findEntityURIs(IKey key, IVersion version) throws RepositoryException {
        RepositoryConnection conn = null;
        try {
            String entityURIVariableName = "_e";
            conn = ipc.getRepository().getConnection();
            TupleQuery tupleQuery =
                conn.prepareTupleQuery(
                    QueryLanguage.SERQL,
                    generateEntitySearchQuery(key, version, entityURIVariableName));
            TupleQueryResult result = tupleQuery.evaluate();
            try {
                Set<URI> returnVal = new HashSet<URI>();
                while (result.hasNext()) {
                    returnVal.add((URI)result.next().getValue(entityURIVariableName));
                }
                return returnVal;
            } finally {
                result.close();
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

    /**
     * method to find and entityURI by contentID
     * 
     * @param contentId the content ID to search for
     * @return the URI of the entity
     * @throws RepositoryException error while running query
     */
    URI findEntityURIbyContentId(String contentId) throws RepositoryException {
        RepositoryConnection conn = null;
        try {
            String entityURIVariableName = "_e";
            conn = ipc.getRepository().getConnection();
            TupleQuery tupleQuery =
                conn.prepareTupleQuery(
                    QueryLanguage.SERQL,
                    generateEntitySearchQuery(contentId, entityURIVariableName));
            TupleQueryResult result = tupleQuery.evaluate();
            try {
                BindingSet resultSet = null;
                int resultSize = 0;
                while (result.hasNext()) {
                    if (resultSize > 1) {
                        throw new NonUniqueResultException();
                    }
                    resultSet = result.next();
                    resultSize++;
                }
                if (resultSet != null) {
                    Value entityURI = resultSet.getValue(entityURIVariableName);
                    return factory.createURI(entityURI.stringValue());
                }
            } finally {
                result.close();
            }
            return null;
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

    /**
     * Find all entityURIs associated with the specific boundary objects,
     * filtered based on entity type.
     * 
     * @param externalIdentifier the boundary object type
     * @param boundaryIdentifierValues the boundary object values
     * @param entityTypes the entity types to filter on
     * @param conn the repository connection
     * @return a map with boundary object values as keys, and lists of URIs that
     *         reference those boundary object as values
     * @throws RepositoryException error while executing query
     */
    Map<String, List<URI>> findEntityUrisFromBoundaryObjectIds(
            IExternalIdentifier externalIdentifier,
            Collection<String> boundaryIdentifierValues,
            Set<? extends IEntityDefinition> entityTypes,
            RepositoryConnection conn) throws RepositoryException {
        try {
            String entityURIVariableName = "_e";
            String boundaryIdVariableName = "_boundaryObjectValue";
            TupleQuery tupleQuery =
                conn.prepareTupleQuery(
                    QueryLanguage.SERQL,
                    generateRelatedBoundaryObjectSearchString(
                        externalIdentifier.getId(),
                        boundaryIdentifierValues,
                        entityTypes,
                        boundaryIdVariableName,
                        entityURIVariableName));
            TupleQueryResult result = tupleQuery.evaluate();
            Map<String, List<URI>> relatedEntityURIs =
                new HashMap<String, List<URI>>();
            try {
                BindingSet resultSet = null;
                while (result.hasNext()) {
                    resultSet = result.next();
                    Value boundaryId =
                        resultSet.getValue(boundaryIdVariableName);
                    Value entityURI = resultSet.getValue(entityURIVariableName);
                    if (!relatedEntityURIs.containsKey(boundaryId.stringValue())) {
                        relatedEntityURIs.put(
                            boundaryId.stringValue(),
                            new LinkedList<URI>());
                    }
                    relatedEntityURIs.get(boundaryId.stringValue()).add(
                        factory.createURI(entityURI.stringValue()));
                }
            } finally {
                result.close();
            }
            return relatedEntityURIs;
        } catch (MalformedQueryException e) {
            throw new RepositoryException(e);
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }

    }

    private String generateRelatedBoundaryObjectSearchString(
            String boudaryObjectType,
            Collection<String> boundaryIdentifierValues,
            Set<? extends IEntityDefinition> entityTypes,
            String boundaryIdVariableName,
            String entityURIVariableName) {
        String entityTypeVariable = "entityType";
        String boundaryObjectVariable = "boundaryObject";

        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(boundaryIdVariableName).append(
            ", ").append(entityURIVariableName).append(" ").append(NEW_LINE);
        // _e hasEntityType {entityType};
        queryBuilder.append("FROM {").append(entityURIVariableName).append("} ");
        queryBuilder.append("  <").append(ontology.HAS_ENTITY_TYPE.URI).append(
            "> ");
        queryBuilder.append("{").append(entityTypeVariable).append("}").append(
            ";").append(NEW_LINE);
        // hasIndirectRelationship {boundaryObject},
        queryBuilder.append("  <").append(
            ontology.HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.URI).append("> ");
        queryBuilder.append("{").append(boundaryObjectVariable).append("}").append(
            ",").append(NEW_LINE);
        // {boundaryObject} hasType {boundaryObjectType};
        queryBuilder.append("{").append(boundaryObjectVariable).append("}").append(
            " ");
        queryBuilder.append("<").append(ontology.HAS_BOUNDARY_OBJECT_TYPE.URI).append(
            "> ");
        queryBuilder.append("{\"").append(boudaryObjectType).append("\"};").append(
            NEW_LINE);
        // hasValue {boundaryObjectValue}
        queryBuilder.append("<").append(ontology.HAS_BOUNDARY_OBJECT_VALUE.URI).append(
            "> ");
        queryBuilder.append("{").append(boundaryIdVariableName).append("}").append(
            " ").append(NEW_LINE);
        // WHERE
        queryBuilder.append("WHERE").append(NEW_LINE);
        if (!entityTypes.isEmpty()) {
            Collection<String> target = new HashSet<String>();
            for (IEntityDefinition ied : entityTypes) {
                target.add(ied.getId());
            }
            queryBuilder.append(entityTypeVariable).append(" IN ").append(
                convertStringSetForInClause(target)).append(NEW_LINE);
            queryBuilder.append("AND").append(NEW_LINE);
        }
        queryBuilder.append(boundaryIdVariableName).append(" IN ").append(
            convertStringSetForInClause(boundaryIdentifierValues)).append(
            NEW_LINE);

        return queryBuilder.toString();
    }

    private static String convertStringSetForInClause(Collection<String> target) {
        StringBuilder inClauseBuilder = new StringBuilder();
        inClauseBuilder.append("(");
        boolean beginning = true;
        for (String atom : target) {
            if (beginning) {
                beginning = false;
            } else {
                inClauseBuilder.append(", ");
            }
            inClauseBuilder.append("\"").append(atom).append("\"");
        }
        inClauseBuilder.append(")");
        return inClauseBuilder.toString();
    }

    private String generateEntitySearchQuery(
            String contentId,
            String entityURIVariableName) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(entityURIVariableName).append(" ").append(
            NEW_LINE);
        // _e hasContentId contentId
        queryBuilder.append("FROM {").append(entityURIVariableName).append("} ");
        queryBuilder.append("<").append(ontology.HAS_CONTENT_ID.URI).append(">");
        queryBuilder.append(" {\"").append(contentId).append("\"}").append(
            NEW_LINE);
        return queryBuilder.toString();
    }

    private String generateEntitySearchQuery(
            IKey key,
            IVersion version,
            String entityURIVariableName) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(entityURIVariableName).append(" ").append(
            NEW_LINE);
        // _e hasKey _key
        queryBuilder.append("FROM {").append(entityURIVariableName).append("} ");
        queryBuilder.append("<").append(ontology.HAS_KEY.URI).append(">");
        queryBuilder.append(" {_key},").append(NEW_LINE);
        // _key hasKeyType _keyType
        queryBuilder.append("{_key} ");
        queryBuilder.append("<").append(ontology.HAS_KEY_TYPE.URI).append(">");
        queryBuilder.append(" {\"").append(key.getId()).append("\"},").append(
            NEW_LINE);
        int fieldNumber = 1;
        for (Map.Entry<String, String> keyFieldEntry : key.getFieldNameToValueMap().entrySet()) {
            if (fieldNumber > 1) {
                queryBuilder.append(",").append(NEW_LINE);
            }
            // _key hasField _fieldX
            String fieldNameDeclaration = "{_field" + fieldNumber + "}";
            queryBuilder.append("{_key} ");
            queryBuilder.append("<").append(ontology.HAS_FIELD_DATA.URI).append(
                ">");
            queryBuilder.append(" ").append(fieldNameDeclaration).append(",").append(
                NEW_LINE);
            // _fieldx hasFieldType type
            queryBuilder.append(fieldNameDeclaration);
            queryBuilder.append(" <").append(ontology.HAS_FIELD_NAME.URI).append(
                ">");
            queryBuilder.append(" {\"").append(keyFieldEntry.getKey()).append(
                "\"},").append(NEW_LINE);
            // _fieldx hasFieldValue value
            queryBuilder.append(fieldNameDeclaration);
            queryBuilder.append(" <").append(ontology.HAS_FIELD_VALUE.URI).append(
                ">");
            queryBuilder.append(" {\"").append(keyFieldEntry.getValue()).append(
                "\"}");
            fieldNumber++;
        }
        if( version != null ) {
            queryBuilder.append(",").append(NEW_LINE);
            queryBuilder.append("{").append(entityURIVariableName).append("} ");
            queryBuilder.append("<").append(ontology.HAS_VERSION.URI).append("> ");
            queryBuilder.append("{versionBNode},").append(NEW_LINE);
            
            queryBuilder.append("{versionBNode} ");
            queryBuilder.append("<").append(ontology.HAS_VERSION_TYPE.URI).append("> ");
            queryBuilder.append("{<").append(version.getDefinition().getMethod().getId()).append(">};").append(NEW_LINE);

            queryBuilder.append("<").append(ontology.HAS_VERSION_VALUE.URI).append("> ");
            queryBuilder.append("{\"").append(version.getValue()).append("\"}").append(NEW_LINE);
        }
        
        return queryBuilder.toString();
    }

}

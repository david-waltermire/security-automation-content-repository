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
package gov.nist.scap.content.semantic.managers;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.KeyException;
import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IKeyedEntityDefinition;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.entity.EntityBuilder;
import gov.nist.scap.content.semantic.exceptions.IncompleteBuildStateException;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.URI;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

/**
 * <p>
 * A manager to coordinate the building of Key from triples originating from a
 * specific entity.
 * </p>
 * 
 * @see IKey
 */
public class KeyStatementManager implements RegenerationStatementManager {
    private MetaDataOntology ontology;
    
    private IPersistenceContext ipc;
    
    private URI owningEntityURI;
    
    private String keyType = null;

    private String entityType = null;

    private Set<Field> fieldSet = new HashSet<Field>();

    /**
     * The default constructor
     * @param ipc the persistence context
     * @param owningEntityURI the owning entity of the relationship
     */
    public KeyStatementManager(IPersistenceContext ipc, URI owningEntityURI) {
        this.ontology = ipc.getOntology();
        this.ipc = ipc;
        this.owningEntityURI = owningEntityURI;
    }

    @Override
    public void populateEntity(EntityBuilder builder) throws RepositoryException {
        builder.setKey(produceKey());
    }

    /**
     * Call to produce the key without populating any entity
     * 
     * @return the produced key
     * @exception RepositoryException error with repository
     */
    public IKey produceKey() throws RepositoryException {

        try {
            StringBuilder queryBuilder = new StringBuilder();
            String entityTypeVar = "_entityType";
            String keyTypeVar = "_keyType";
            String fieldName = "_fieldName";
            String fieldValue = "_fieldValue";
            queryBuilder.append("SELECT DISTINCT ").append(entityTypeVar).append(", ").append(keyTypeVar).append(", ").append(fieldName).append(", ").append(fieldValue).append(" FROM ").append("\n");
            queryBuilder.append("{<").append(owningEntityURI).append(">} ").append("<").append(ontology.HAS_ENTITY_TYPE.URI).append(">").append(" {").append(entityTypeVar).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_KEY.URI).append(">").append(" {_keyBNode},").append("\n");
            queryBuilder.append("{_keyBNode}").append("<").append(ontology.HAS_KEY_TYPE.URI).append(">").append(" {").append(keyTypeVar).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_FIELD_DATA.URI).append(">").append(" {_fieldBNode},").append("\n");
            queryBuilder.append("{_fieldBNode}").append("<").append(ontology.HAS_FIELD_NAME.URI).append(">").append(" {").append(fieldName).append("};").append("\n");
            queryBuilder.append("<").append(ontology.HAS_FIELD_VALUE.URI).append(">").append(" {").append(fieldValue).append("}").append("\n");
            TupleQuery tupleQuery =
                ipc.getRepository().getConnection().prepareTupleQuery(
                    QueryLanguage.SERQL,
                    queryBuilder.toString());
            TupleQueryResult result = tupleQuery.evaluate();
            BindingSet resultSet = null;
            
            while (result.hasNext()) {
                resultSet = result.next();
                if( keyType == null ) {
                    keyType = resultSet.getValue(keyTypeVar).stringValue();
                } else if( !keyType.equals(resultSet.getValue(keyTypeVar).stringValue()) ){
                    throw new RepositoryException("Found 2 key types for the same entity. Entity " + owningEntityURI.stringValue());
                }
                if( entityType == null ) {
                    entityType = resultSet.getValue(entityTypeVar).stringValue();
                } else if( !entityType.equals(resultSet.getValue(entityTypeVar).stringValue()) ){
                    throw new RepositoryException("Found 2 entity types for the same entity. Entity " + owningEntityURI.stringValue());
                }
                fieldSet.add(new Field(resultSet.getValue(fieldName).stringValue(), resultSet.getValue(fieldValue).stringValue()));
            }
        } catch (MalformedQueryException e) {
            throw new RepositoryException(e);
        } catch (QueryEvaluationException e) {
            throw new RepositoryException(e);
        }

        if( entityType != null ) {
            IEntityDefinition ied = ontology.getEntityDefinitionById(entityType);
            if (ied instanceof IKeyedEntityDefinition) {
                KeyBuilder builder =
                    new KeyBuilder(
                        ((IKeyedEntityDefinition)ied).getKeyDefinition().getFields());
                builder.setId(keyType);
                populateFields(builder);
                try {
                    return builder.toKey();
                } catch (KeyException e) {
                    throw new IncompleteBuildStateException(e);
                }
            }
            throw new IncompleteBuildStateException(
                "Attempted to build key of a non-keyed type");
        }
        return null;
    }

    private void populateFields(KeyBuilder builder) {
        for (Field field : fieldSet) {
            builder.addField(field.getFieldId(), field.getFieldValue());
        }
    }

    private static class Field {
        private final String fieldId;
        private final String fieldValue;

        Field(String fieldId, String fieldValue) {
            this.fieldId = fieldId;
            this.fieldValue = fieldValue;
        }

        String getFieldId() {
            return fieldId;
        }

        String getFieldValue() {
            return fieldValue;
        }

    }

}

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
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.entity.EntityBuilder;
import gov.nist.scap.content.semantic.exceptions.IncompleteBuildStateException;

import java.util.LinkedHashMap;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

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
    private String hasEntityType;
    private String hasKeyType;
    private boolean hasKey = false;

    private LinkedHashMap<String, Field> fieldUriToFieldMap =
        new LinkedHashMap<String, Field>();

    /**
     * the default constructor
     * @param ontology the metadata model
     */
    public KeyStatementManager(MetaDataOntology ontology) {
        this.ontology = ontology;
    }

    @Override
    public boolean scan(Statement statement) {
        URI predicate = statement.getPredicate();
        if (predicate.equals(ontology.HAS_KEY.URI)) {
            hasKey = true;
            return true; // return true to claim ownership of statement, nothing
                         // to do though
        }
        if (predicate.equals(ontology.HAS_ENTITY_TYPE.URI)) {
            hasEntityType = statement.getObject().stringValue();
            return true;
        }
        if (predicate.equals(ontology.HAS_KEY_TYPE.URI)) {
            hasKeyType = statement.getObject().stringValue();
            return true;
        }
        if (predicate.equals(ontology.HAS_FIELD_DATA.URI)) {
            // do nothing because of the context, we're only processing 1 entity
            // anyway, so this statements doesn't provide an value
            return true;
        }
        if (predicate.equals(ontology.HAS_FIELD_NAME.URI)) {
            populateFieldType(statement);
            return true;
        }
        if (predicate.equals(ontology.HAS_FIELD_VALUE.URI)) {
            populateFieldValue(statement);
            return true;
        }
        return false;
    }

    @Override
    public void populateEntity(EntityBuilder builder) {
        if (hasKey)
            builder.setKey(produceKey());
    }

    /**
     * Call to produce the key without populating any entity
     * 
     * @return the produced key
     */
    public IKey produceKey() {
        IEntityDefinition ied = ontology.getEntityDefinitionById(hasEntityType);
        if (ied instanceof IKeyedEntityDefinition) {
            KeyBuilder builder =
                new KeyBuilder(
                    ((IKeyedEntityDefinition)ied).getKeyDefinition().getFields());
            builder.setId(hasKeyType);
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

    private void populateFields(KeyBuilder builder) {
        for (Field field : fieldUriToFieldMap.values()) {
            builder.addField(field.getFieldId(), field.getFieldValue());
        }
    }

    private void populateFieldType(Statement statement) {
        String fieldURI = statement.getSubject().stringValue();
        String fieldType = statement.getObject().stringValue();
        Field fieldEntry = fieldUriToFieldMap.get(fieldURI);
        if (fieldEntry == null) {
            fieldEntry = new Field();
            fieldUriToFieldMap.put(fieldURI, fieldEntry);
        }
        fieldEntry.setFieldId(fieldType);
    }

    private void populateFieldValue(Statement statement) {
        String fieldURI = statement.getSubject().stringValue();
        String fieldValue = statement.getObject().stringValue();
        Field fieldEntry = fieldUriToFieldMap.get(fieldURI);
        if (fieldEntry == null) {
            fieldEntry = new Field();
            fieldUriToFieldMap.put(fieldURI, fieldEntry);
        }
        fieldEntry.setFieldValue(fieldValue);
    }

    private static class Field {
        private String fieldId;
        private String fieldValue;

        void setFieldId(String fieldId) {
            this.fieldId = fieldId;
        }

        String getFieldId() {
            return fieldId;
        }

        void setFieldValue(String fieldValue) {
            this.fieldValue = fieldValue;
        }

        String getFieldValue() {
            return fieldValue;
        }

    }

}

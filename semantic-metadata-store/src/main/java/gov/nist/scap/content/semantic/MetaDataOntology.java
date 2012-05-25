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

import gov.nist.scap.content.model.definitions.IEntityDefinition;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.model.definitions.IRelationshipDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang3.text.WordUtils;
import org.openrdf.model.BNode;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

/**
 * Contains necessary assertions to model and build meta-data model in RDFS.
 * TODO: Everything in this class should eventually be put into an RDFS ontology
 * file TODO: figure out way to make model constructs final static; currently
 * can't do this because valueFactory instance is required to create them.
 */
public class MetaDataOntology implements IMetadataModel {
    private ValueFactory factory;
    // TODO should we version this?
    private static final String BASE_MODEL_URI =
        "http://scap.nist.gov/resource/content/model#";

    // classes in model
    public final Construct ENTITY_CLASS;
    /** key of an entity */
    public final Construct KEY_CLASS;
    /** field of a key (to include both field id and value) */
    public final Construct FIELD_CLASS;
    public final Construct PROPERTY_CLASS;
    /** same as 'externalId' from java model */
    public final Construct BOUNDARY_OBJECT_CLASS;
    public final Construct VERSION_CLASS;

    // ** relationships in model **
    /** persistence ID of an entity */
    public final Predicate HAS_CONTENT_ID;
    /** type of entity. TODO: this should be refactored into type hiearchy */
    public final Predicate HAS_ENTITY_TYPE;
    /** key of an entity */
    public final Predicate HAS_KEY;
    /** type of key.....this should be refactored into a class heiarch??? */
    public final Predicate HAS_KEY_TYPE;
    /** n-ary relationship between a key and the field_id/value */
    public final Predicate HAS_FIELD_DATA;
    /** name of a key field */
    public final Predicate HAS_FIELD_NAME;
    /** value of a field (for a specific field type). */
    public final Predicate HAS_FIELD_VALUE;
    public final Predicate HAS_PROPERTY;
    public final Predicate HAS_PROPERTY_NAME;
    public final Predicate HAS_PROPERTY_VALUE;
    /** range should always be a boundary object */
    public final Predicate HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO;
    /** entity to entity relationship */
    public final Predicate HAS_KEYED_RELATIONSHIP_TO;
    /** Composite relationships are always parent to child right now */
    public final Predicate HAS_COMPOSITE_RELATIONSHIP_TO;
    /**
     * Not actually a different type of relationship from the Java model, but
     * the inverse of child
     */
    public final Predicate HAS_PARENT_RELATIONSHIP_TO;
    /**
     * the type of the boundaryObject (or externalId in terms of java
     * model)...TODO: this type relationship should be turned into a class
     * heiarchy.
     */
    public final Predicate HAS_BOUNDARY_OBJECT_TYPE;
    /** the actual value of the boundary object (e.g., CCE-XXXXX-X, CPE:/... */
    public final Predicate HAS_BOUNDARY_OBJECT_VALUE;

    public final Predicate HAS_VERSION;
    public final Predicate HAS_VERSION_TYPE;
    public final Predicate HAS_VERSION_VALUE;

    // dynamic predicates
    private Map<String, Construct> boundaryObjectRelationships =
        new HashMap<String, Construct>();
    private Map<String, Construct> keyedRelationships =
        new HashMap<String, Construct>();
    private Map<String, Construct> compositeRelationships =
        new HashMap<String, Construct>();

    private IMetadataModel javaModel = null;

    private BNode context;

    /**
     * default constructor
     * 
     * @param factory the value factory
     */
    MetaDataOntology(ValueFactory factory) {
        this.factory = factory;
        context = factory.createBNode();

        // define classes
        ENTITY_CLASS = new Construct(genModelURI("entity"), "entity");
        KEY_CLASS = new Construct(genModelURI("Key"), "Key");
        FIELD_CLASS = new Construct(genModelURI("Field"), "Field");
        PROPERTY_CLASS = new Construct(genModelURI("Property"), "Property");
        BOUNDARY_OBJECT_CLASS =
            new Construct(genModelURI("BoundaryObject"), "Boundary Object");
        VERSION_CLASS = new Construct(genModelURI("Version"), "Version");

        // base entity info
        HAS_CONTENT_ID =
            new Predicate(genModelURI("hasContentId"), "hasContentId").addDomain(ENTITY_CLASS);
        HAS_ENTITY_TYPE =
            new Predicate(genModelURI("hasEntityType"), "hasEntityType").addDomain(ENTITY_CLASS);

        // relationships
        HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO =
            new Predicate(
                genModelURI("hasBoundaryObjectRelationshipTo"),
                "hasBoundaryObjectRelationshipTo");
        HAS_KEYED_RELATIONSHIP_TO =
            new Predicate(
                genModelURI("hasKeyedRelationshipTo"),
                "hasKeyedRelationshipTo").addDomain(ENTITY_CLASS).addRange(
                ENTITY_CLASS);
        HAS_COMPOSITE_RELATIONSHIP_TO =
            new Predicate(
                genModelURI("hasChildRelationshipTo"),
                "hasChildRelationshipTo").addDomain(ENTITY_CLASS).addRange(
                ENTITY_CLASS);
        HAS_PARENT_RELATIONSHIP_TO =
            new Predicate(
                genModelURI("hasParentRelationshipTo"),
                "hasParentRelationshipTo").addDomain(ENTITY_CLASS).addRange(
                ENTITY_CLASS);

        // key
        HAS_KEY =
            new Predicate(genModelURI("hasKey"), "hasKey").addDomain(
                ENTITY_CLASS).addRange(KEY_CLASS);
        HAS_KEY_TYPE =
            new Predicate(genModelURI("hasKeyType"), "hasKeyType").addDomain(KEY_CLASS);

        // field
        HAS_FIELD_DATA =
            new Predicate(genModelURI("hasFieldData"), "hasFieldData").addDomain(
                KEY_CLASS).addRange(FIELD_CLASS);
        HAS_FIELD_NAME =
            new Predicate(genModelURI("hasFieldName"), "hasFieldName").addDomain(FIELD_CLASS);
        HAS_FIELD_VALUE =
            new Predicate(genModelURI("hasFieldValue"), "hasFieldValue").addDomain(FIELD_CLASS);

        // property
        HAS_PROPERTY =
                new Predicate(genModelURI("hasProperty"), "hasProperty").addDomain(ENTITY_CLASS).addRange(PROPERTY_CLASS);
        HAS_PROPERTY_NAME =
                new Predicate(genModelURI("hasPropertyName"), "hasPropertyName").addDomain(PROPERTY_CLASS);
        HAS_PROPERTY_VALUE =
                new Predicate(genModelURI("hasPropertyValue"), "hasPropertyValue").addDomain(PROPERTY_CLASS);

        // boundary object
        HAS_BOUNDARY_OBJECT_TYPE =
            new Predicate(
                genModelURI("hasBoundaryObjectType"),
                "hasBoundaryObjectType").addDomain(BOUNDARY_OBJECT_CLASS);
        HAS_BOUNDARY_OBJECT_VALUE =
            new Predicate(
                genModelURI("hasBoundaryObjectValue"),
                "hasBoundaryObjectValue").addDomain(BOUNDARY_OBJECT_CLASS);

        // version
        HAS_VERSION =
            new Predicate(genModelURI("hasVersion"), "hasVersion").addDomain(
                ENTITY_CLASS).addRange(VERSION_CLASS);
        HAS_VERSION_TYPE =
            new Predicate(genModelURI("hasVersionType"), "hasVersionType").addDomain(VERSION_CLASS);
        HAS_VERSION_VALUE =
            new Predicate(genModelURI("hasVersionValue"), "hasVersionValue").addDomain(VERSION_CLASS);
    }

    /**
     * helper method to load all model triples into triple store using given
     * connection
     * 
     * @param conn respository connection, pass in null if the statements have
     *            already been persisted
     * @param javaModel the model to load
     * @throws RepositoryException error accessing the repository
     */
    void loadModel(RepositoryConnection conn, IMetadataModel javaModel)
            throws RepositoryException {
        this.javaModel = javaModel;

        List<Statement> statements = new LinkedList<Statement>();
        // assert dynamic predicates
        statements.addAll(loadBoundaryObjectRelationships(javaModel.getBoundaryIndentifierRelationshipIds()));
        statements.addAll(loadKeyedRelationships(javaModel.getKeyedRelationshipIds()));
        statements.addAll(loadCompositeRelationships(javaModel.getCompositeRelationshipIds()));

        if (conn != null) {

            try {
                for (Field f : this.getClass().getFields()) {
                    if (Modifier.isFinal(f.getModifiers())
                        && Modifier.isPublic(f.getModifiers())) {
                        if (f.getType().equals(Construct.class)) {
                            statements.add(factory.createStatement(
                                ((Construct)f.get(this)).URI,
                                RDF.TYPE,
                                RDFS.CLASS));
                        } else if (f.getType().equals(Predicate.class)) {
                            Predicate p = (Predicate)f.get(this);
                            statements.add(factory.createStatement(
                                p.URI,
                                RDF.TYPE,
                                RDF.PROPERTY));
                            for( Construct c : p.getDomain() ) {
                                statements.add(factory.createStatement(
                                    p.URI,
                                    RDFS.DOMAIN,
                                    c.URI));
                            }
                            for( Construct c : p.getRange() ) {
                                statements.add(factory.createStatement(
                                    p.URI,
                                    RDFS.RANGE,
                                    c.URI));
                            }
                        }
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("misconfigured ontology...",e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("misconfigured ontology...",e);
            }
            conn.add(statements, context);
            conn.commit();
            conn.close();
        }
    }

    /**
     * Returns the model construct representing the predicate associated with
     * the given relationship ID.
     * 
     * @param relId the ID of the relationship
     * @return URI of the predicate
     */
    public URI findIndirectRelationshipURI(String relId) {
        return boundaryObjectRelationships.get(relId).URI;
    }

    /**
     * Returns the model construct representing the predicate associated with
     * the given relationship ID.
     * 
     * @param relId the ID of the relationship
     * @return URI of the predicate
     */
    public URI findKeyedRelationshipURI(String relId) {
        return keyedRelationships.get(relId).URI;
    }

    /**
     * Returns the model construct representing the predicate associated with
     * the given relationship ID.
     * 
     * @param relId the ID of the relationship
     * @return URI of the predicate
     */
    public URI findCompositeRelationshipURI(String relId) {
        return compositeRelationships.get(relId).URI;
    }

    @Override
    public Collection<String> getBoundaryIndentifierRelationshipIds() {
        if (javaModel != null) {
            return javaModel.getBoundaryIndentifierRelationshipIds();
        }
        return null;
    }

    @Override
    public Collection<String> getCompositeRelationshipIds() {
        if (javaModel != null) {
            return javaModel.getCompositeRelationshipIds();
        }
        return null;
    }

    @Override
    public IExternalIdentifier getExternalIdentifierById(String externalIdType) {
        if (javaModel != null) {
            return javaModel.getExternalIdentifierById(externalIdType);
        }
        return null;
    }

    @Override
    public Collection<String> getKeyedRelationshipIds() {
        if (javaModel != null) {
            return javaModel.getKeyedRelationshipIds();
        }
        return null;
    }

    @Override
    public <T extends IRelationshipDefinition> T getRelationshipDefinitionById(
            String keyedRelationshipId) {
        if (javaModel != null) {
            return javaModel.getRelationshipDefinitionById(keyedRelationshipId);
        }
        return null;
    }

    @Override
    public <T extends IEntityDefinition> T getEntityDefinitionById(
            String entityType) {
        if (javaModel != null) {
            return javaModel.getEntityDefinitionById(entityType);
        }
        return null;
    }

    private List<Statement> loadBoundaryObjectRelationships(
            Collection<String> boundaryObjectRelationshipIds) {
        List<Statement> statements = new LinkedList<Statement>();
        for (String id : boundaryObjectRelationshipIds) {
            // this assumes a URN will be accepted as a valid URI.
            Construct relationshipConstruct =
                new Construct(factory.createURI(id), extractLabelFromRelId(id));
            boundaryObjectRelationships.put(id, relationshipConstruct);
            statements.add(factory.createStatement(
                relationshipConstruct.URI,
                RDFS.SUBPROPERTYOF,
                HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.URI));
        }
        return statements;
    }

    private List<Statement> loadKeyedRelationships(
            Collection<String> keyedRelationshipIds) {
        List<Statement> statements = new LinkedList<Statement>();
        for (String id : keyedRelationshipIds) {
            // this assumes a URN will be accepted as a valid URI.
            Construct relationshipConstruct =
                new Construct(factory.createURI(id), extractLabelFromRelId(id));
            keyedRelationships.put(id, relationshipConstruct);
            statements.add(factory.createStatement(
                relationshipConstruct.URI,
                RDFS.SUBPROPERTYOF,
                HAS_KEYED_RELATIONSHIP_TO.URI));
        }
        return statements;
    }

    private List<Statement> loadCompositeRelationships(
            Collection<String> compositeRelationshipIds) {
        List<Statement> statements = new LinkedList<Statement>();
        for (String id : compositeRelationshipIds) {
            // this assumes a URN will be accepted as a valid URI.
            Construct relationshipConstruct =
                new Construct(factory.createURI(id), extractLabelFromRelId(id));
            compositeRelationships.put(id, relationshipConstruct);
            statements.add(factory.createStatement(
                relationshipConstruct.URI,
                RDFS.SUBPROPERTYOF,
                HAS_COMPOSITE_RELATIONSHIP_TO.URI));
        }
        return statements;
    }

    private static String extractLabelFromRelId(String id) {
        int pos = id.lastIndexOf(":") + 1;
        StringTokenizer tok = new StringTokenizer(id.substring(pos), "-");
        StringBuilder labelBuilder = new StringBuilder();
        labelBuilder.append("has");
        while (tok.hasMoreElements()) {
            String element = (String)tok.nextElement();
            labelBuilder.append(WordUtils.capitalize(element));
        }
        return labelBuilder.toString();
    }

    private URI genModelURI(String specificPart) {
        return factory.createURI(BASE_MODEL_URI + specificPart);
    }

    /**
     * A grouping of URIs and labels
     * 
     * @author Adam Halbardier
     */
    public static class Construct {
        public final URI URI;
        public final String LABEL;

        /**
         * default constructor
         * 
         * @param uri uri
         * @param label label
         */
        private Construct(URI uri, String label) {
            this.URI = uri;
            this.LABEL = label;
        }
    }

    /**
     * Add domain and range to Construct
     * 
     * @author Adam Halbardier
     */
    public static class Predicate extends Construct {
        private List<Construct> domain = new LinkedList<Construct>();
        private List<Construct> range = new LinkedList<Construct>();

        private Predicate(URI uri, String label) {
            super(uri, label);
        }

        private Predicate addDomain(Construct c) {
            domain.add(c);
            return this;
        }

        private Predicate addRange(Construct c) {
            range.add(c);
            return this;
        }

        private List<Construct> getDomain() {
            return Collections.unmodifiableList(domain);
        }

        private List<Construct> getRange() {
            return Collections.unmodifiableList(range);
        }

    }
}

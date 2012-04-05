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

import java.util.Collection;
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
 * 
 * TODO: Everything in this class should eventually be put into an RDFS ontology file
 * TODO: figure out way to make model constructs final static; 
 * currently can't do this because valueFactory instance is required to create them.
 */
public class MetaDataOntology implements IMetadataModel {
	private ValueFactory factory;
	//TODO should we version this?
	private static final String BASE_MODEL_URI = "http://scap.nist.gov/resource/content/model#";
	
	//classes in model
	public final Construct ENTITY_CLASS;
	/** key of an entity*/
	public final Construct KEY_CLASS;  
	/** field of a key (to include both field id and value) */
	public final Construct FIELD_CLASS; 
	/** same as 'externalId' from java model*/
	public final Construct BOUNDARY_OBJECT_CLASS; 
	
	//** relationships in model **
	/** persistence ID of an entity*/
	public final Construct HAS_CONTENT_ID; 
	/** type of entity.  TODO: this should be refactored into type hiearchy */
	public final Construct HAS_ENTITY_TYPE;
	/** key of an entity*/
	public final Construct HAS_KEY; 
	/** type of key.....this should be refactored into a class heiarch??? */
	public final Construct HAS_KEY_TYPE; 
	/**  n-ary relationship between a key and the field_id/value*/
	public final Construct HAS_FIELD_DATA;
	/** name of a key field*/
	public final Construct HAS_FIELD_NAME; 
	/** value of a field (for a specific field type).*/
	public final Construct HAS_FIELD_VALUE; 
	/** range should always be a boundary object*/
	public final Construct HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO; 
	/** entity to entity relationship*/
	public final Construct HAS_KEYED_RELATIONSHIP_TO; 
    /** Composite relationships are always parent to child right now */
    public final Construct HAS_COMPOSITE_RELATIONSHIP_TO; 
    /** Not actually a different type of relationship from the Java model, but the inverse of child */
    public final Construct HAS_PARENT_RELATIONSHIP_TO; 
    /** the type of the boundaryObject (or externalId in terms of java model)...TODO: this type relationship should be turned into a class heiarchy.  */
	public final Construct HAS_BOUNDARY_OBJECT_TYPE; 
	/** the actual value of the boundary object (e.g., CCE-XXXX, CPE:/XXX:XXX */
	public final Construct HAS_BOUNDARY_OBJECT_VALUE; 
	
    public final Construct HAS_VERSION; 
    public final Construct HAS_VERSION_TYPE; 
    public final Construct HAS_VERSION_VALUE; 
	
	//dynamic predicates
	private Map<String, Construct> boundaryObjectRelationships = new HashMap<String, Construct>();
	private Map<String, Construct> keyedRelationships = new HashMap<String, Construct>();
	private Map<String, Construct> compositeRelationships = new HashMap<String, Construct>();
	
	private IMetadataModel javaModel = null;
	
	private BNode context;
    
	/**
	 * default constructor
	 * @param factory the value factory
	 */
	MetaDataOntology(ValueFactory factory) {
		this.factory = factory;
		context = factory.createBNode();

		
		// define classes
		ENTITY_CLASS = new Construct(genModelURI("entity"), "entity");
		KEY_CLASS = new Construct(genModelURI("Key"), "Key");
		FIELD_CLASS = new Construct(genModelURI("Field"), "Field");
		BOUNDARY_OBJECT_CLASS = new Construct(genModelURI("BoundaryObject"), "Boundary Object");
		
		
		// define relationships
		HAS_CONTENT_ID = new Construct(genModelURI("hasContentId"), "hasContentId");
		HAS_ENTITY_TYPE = new Construct(genModelURI("hasEntityType"), "hasEntityType");
		HAS_KEY = new Construct(genModelURI("hasKey"), "hasKey");
		HAS_KEY_TYPE = new Construct(genModelURI("hasKeyType"), "hasKeyType");
		HAS_FIELD_DATA = new Construct(genModelURI("hasFieldData"), "hasFieldData");
		HAS_FIELD_NAME = new Construct(genModelURI("hasFieldName"), "hasFieldName");
		HAS_FIELD_VALUE = new Construct(genModelURI("hasFieldValue"), "hasFieldValue");
		HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO = new Construct(genModelURI("hasBoundaryObjectRelationshipTo"), "hasBoundaryObjectRelationshipTo");
		HAS_KEYED_RELATIONSHIP_TO = new Construct(genModelURI("hasKeyedRelationshipTo"), "hasKeyedRelationshipTo");
		HAS_COMPOSITE_RELATIONSHIP_TO = new Construct(genModelURI("hasChildRelationshipTo"), "hasChildRelationshipTo");
		HAS_PARENT_RELATIONSHIP_TO = new Construct(genModelURI("hasParentRelationshipTo"), "hasParentRelationshipTo");
		HAS_BOUNDARY_OBJECT_TYPE = new Construct(genModelURI("hasBoundaryObjectType"), "hasBoundaryObjectType");
		HAS_BOUNDARY_OBJECT_VALUE = new Construct(genModelURI("hasBoundaryObjectValue"), "hasBoundaryObjectValue");
	    HAS_VERSION = new Construct(genModelURI("hasVersion"), "hasVersion");
	    HAS_VERSION_TYPE = new Construct(genModelURI("hasVersionType"), "hasVersionType");
	    HAS_VERSION_VALUE = new Construct(genModelURI("hasVersionValue"), "hasVersionValue");
	}
	
	/**
	 * helper method to load all model triples into triple store using given connection
	 * @param conn respository connection
	 * @param javaModel the model to load
	 * @throws RepositoryException error accessing the repository
	 */
	void loadModel(RepositoryConnection conn, IMetadataModel javaModel) throws RepositoryException {
		// add statements
		List<Statement> statements = new LinkedList<Statement>();
		
		// assert Classes
		statements.addAll(createClass(ENTITY_CLASS.URI, ENTITY_CLASS.LABEL));
		statements.addAll(createClass(KEY_CLASS.URI, KEY_CLASS.LABEL));
		statements.addAll(createClass(FIELD_CLASS.URI, FIELD_CLASS.LABEL));
		statements.addAll(createClass(BOUNDARY_OBJECT_CLASS.URI, BOUNDARY_OBJECT_CLASS.LABEL));
		
		// assert Predicates
		statements.addAll(createPredicate(HAS_CONTENT_ID.URI, HAS_CONTENT_ID.LABEL));
		statements.addAll(createPredicate(HAS_ENTITY_TYPE.URI, HAS_ENTITY_TYPE.LABEL));
		statements.addAll(createPredicate(HAS_KEY.URI, HAS_KEY.LABEL));
		statements.addAll(createPredicate(HAS_KEY_TYPE.URI, HAS_KEY_TYPE.LABEL));
		statements.addAll(createPredicate(HAS_FIELD_DATA.URI, HAS_FIELD_DATA.LABEL));
		statements.addAll(createPredicate(HAS_FIELD_NAME.URI, HAS_FIELD_NAME.LABEL));
		statements.addAll(createPredicate(HAS_FIELD_VALUE.URI, HAS_FIELD_VALUE.LABEL));
		statements.addAll(createPredicate(HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.URI, HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.LABEL));
		statements.addAll(createPredicate(HAS_KEYED_RELATIONSHIP_TO.URI, HAS_KEYED_RELATIONSHIP_TO.LABEL));
        statements.addAll(createPredicate(HAS_COMPOSITE_RELATIONSHIP_TO.URI, HAS_COMPOSITE_RELATIONSHIP_TO.LABEL));
        statements.addAll(createPredicate(HAS_PARENT_RELATIONSHIP_TO.URI, HAS_PARENT_RELATIONSHIP_TO.LABEL));
		statements.addAll(createPredicate(HAS_BOUNDARY_OBJECT_TYPE.URI, HAS_BOUNDARY_OBJECT_TYPE.LABEL));
		statements.addAll(createPredicate(HAS_BOUNDARY_OBJECT_VALUE.URI, HAS_BOUNDARY_OBJECT_VALUE.LABEL));

        statements.addAll(createPredicate(HAS_VERSION.URI, HAS_VERSION.LABEL));
        statements.addAll(createPredicate(HAS_VERSION_TYPE.URI, HAS_VERSION_TYPE.LABEL));
        statements.addAll(createPredicate(HAS_VERSION_VALUE.URI, HAS_VERSION_VALUE.LABEL));

		//assert dynamic predicates
		statements.addAll(loadBoundaryObjectRelationships(javaModel.getBoundaryIndentifierRelationshipIds()));
		statements.addAll(loadKeyedRelationships(javaModel.getKeyedRelationshipIds()));
        statements.addAll(loadCompositeRelationships(javaModel.getCompositeRelationshipIds()));
        
        this.javaModel = javaModel;
		
		conn.add(statements, context);
	}
	

	
	/**
	 * Returns the model construct representing the predicate associated with the given relationship ID.
	 * @param relId the ID of the relationship
	 * @return URI of the predicate
	 */
	public URI findIndirectRelationshipURI(String relId){
		return boundaryObjectRelationships.get(relId).URI;
	}
	
	/**
	 * Returns the model construct representing the predicate associated with the given relationship ID.
     * @param relId the ID of the relationship
     * @return URI of the predicate
	 */
	public URI findKeyedRelationshipURI(String relId){
		return keyedRelationships.get(relId).URI;
	}

	   /**
     * Returns the model construct representing the predicate associated with the given relationship ID.
     * @param relId the ID of the relationship
     * @return URI of the predicate
     */
    public URI findCompositeRelationshipURI(String relId){
        return compositeRelationships.get(relId).URI;
    }

    @Override
    public Collection<String> getBoundaryIndentifierRelationshipIds() {
        if( javaModel != null ) {
            return javaModel.getBoundaryIndentifierRelationshipIds();
        }
        return null;
    }
    
    @Override
    public Collection<String> getCompositeRelationshipIds() {
        if( javaModel != null ) {
            return javaModel.getCompositeRelationshipIds();
        }
        return null;
    }
    
    @Override
    public IExternalIdentifier getExternalIdentifierById(String externalIdType) {
        if( javaModel != null ) {
            return javaModel.getExternalIdentifierById(externalIdType);
        }
        return null;
    }
    
    @Override
    public Collection<String> getKeyedRelationshipIds() {
        if( javaModel != null ) {
            return javaModel.getKeyedRelationshipIds();
        }
        return null;
    }
    
    @Override
    public <T extends IRelationshipDefinition> T getRelationshipDefinitionById(String keyedRelationshipId) {
        if( javaModel != null ) {
            return javaModel.getRelationshipDefinitionById(keyedRelationshipId);
        }
        return null;
    }

    @Override
    public <T extends IEntityDefinition> T getEntityDefinitionById(String entityType) {
        if( javaModel != null ) {
            return javaModel.getEntityDefinitionById(entityType);
        }
        return null;
    }


	private List<Statement> loadBoundaryObjectRelationships(Collection<String> boundaryObjectRelationshipIds){
		List<Statement> statements = new LinkedList<Statement>();
		for (String id : boundaryObjectRelationshipIds){
			//this assumes a URN will be accepted as a valid URI.  TODO: make label more readable
			Construct relationshipConstruct = new Construct(factory.createURI(id), extractLabelFromRelId(id));
			boundaryObjectRelationships.put(id, relationshipConstruct);
			statements.add(factory.createStatement(relationshipConstruct.URI, RDFS.SUBPROPERTYOF, HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.URI));
			statements.add(factory.createStatement(relationshipConstruct.URI, RDFS.LABEL, factory.createLiteral(relationshipConstruct.LABEL)));
		}
		return statements;
	}
	
	private List<Statement> loadKeyedRelationships(Collection<String> keyedRelationshipIds){
		List<Statement> statements = new LinkedList<Statement>();
		for (String id : keyedRelationshipIds){
			//this assumes a URN will be accepted as a valid URI.  TODO: make label more readable
			Construct relationshipConstruct = new Construct(factory.createURI(id), extractLabelFromRelId(id));
			keyedRelationships.put(id, relationshipConstruct);
			statements.add(factory.createStatement(relationshipConstruct.URI, RDFS.SUBPROPERTYOF, HAS_KEYED_RELATIONSHIP_TO.URI));
			statements.add(factory.createStatement(relationshipConstruct.URI, RDFS.LABEL, factory.createLiteral(relationshipConstruct.LABEL)));
		}
		return statements;
	}

    private List<Statement> loadCompositeRelationships(Collection<String> compositeRelationshipIds){
        List<Statement> statements = new LinkedList<Statement>();
        for (String id : compositeRelationshipIds){
            //this assumes a URN will be accepted as a valid URI.  TODO: make label more readable
            Construct relationshipConstruct = new Construct(factory.createURI(id), extractLabelFromRelId(id));
            compositeRelationships.put(id, relationshipConstruct);
            statements.add(factory.createStatement(relationshipConstruct.URI, RDFS.SUBPROPERTYOF, HAS_COMPOSITE_RELATIONSHIP_TO.URI));
            statements.add(factory.createStatement(relationshipConstruct.URI, RDFS.LABEL, factory.createLiteral(relationshipConstruct.LABEL)));
        }
        return statements;
    }

	private static String extractLabelFromRelId(String id) {
		int pos = id.lastIndexOf(":") + 1;
		StringTokenizer tok = new StringTokenizer(id.substring(pos), "-");
		StringBuilder labelBuilder = new StringBuilder();
		labelBuilder.append("has");
		while (tok.hasMoreElements()){
			String element = (String)tok.nextElement();
			labelBuilder.append(WordUtils.capitalize(element));
		}
		return labelBuilder.toString();
	}

	private List<Statement> createClass(URI classUri, String label){
		List<Statement> statements = new LinkedList<Statement>();
		statements.add(factory.createStatement(classUri, RDF.TYPE, RDFS.CLASS));
		statements.add(factory.createStatement(classUri, RDFS.LABEL, factory.createLiteral(label)));
		return statements;
	}
	
	private List<Statement> createPredicate(URI predicateUri, String label){
		List<Statement> statements = new LinkedList<Statement>();
		statements.add(factory.createStatement(predicateUri, RDF.TYPE, RDF.PROPERTY));
		statements.add(factory.createStatement(predicateUri, RDFS.LABEL, factory.createLiteral(label)));
		return statements;
	}
	
	private URI genModelURI(String specificPart){
		return factory.createURI(BASE_MODEL_URI + specificPart);
	}
	
	//simple way to group a things URI and label together
	public static class Construct {
		public final URI URI;
		public final String LABEL;
		
		/**
		 * default constructor
		 * @param uri uri
		 * @param label label
		 */
		public Construct(URI uri, String label) {
			this.URI = uri;
			this.LABEL = label;
		}
	}
}

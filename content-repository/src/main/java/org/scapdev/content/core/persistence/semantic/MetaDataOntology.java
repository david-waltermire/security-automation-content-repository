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
package org.scapdev.content.core.persistence.semantic;

import java.util.LinkedList;
import java.util.List;

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
 * TODO: figure out way to make model constructs final static; 
 * currently can't do this because valueFactory instance is required to create them.
 */
public class MetaDataOntology {
	private ValueFactory factory;
	private static final String BASE_MODEL_URI = "http://scap.nist.gov/cms/model#";
	
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
	/** key of an entity*/
	public final Construct HAS_KEY; 
	/** type of key.....this should be refactored into a class heiarch??? */
	public final Construct HAS_KEY_TYPE; 
	/**  n-ary relationship between a key and the field_id/value*/
	public final Construct HAS_FIELD_DATA;
	/** type (or id) of a key field*/
	public final Construct HAS_FIELD_TYPE; 
	/** value of a field (for a specific field type).*/
	public final Construct HAS_FIELD_VALUE; 
	/** Equivalent to the IndirectRelationship class...range should always be a boundary object*/
	public final Construct HAS_INDIRECT_RELATIONSHIP_TO; 
	/** entity to entity relationship*/
	public final Construct HAS_DIRECT_RELATIONSHIP_TO; 
	/** the type of the boundaryObject (or externalId in terms of java model)...TODO: this type relationship should be turned into a class heiarchy.  */
	public final Construct HAS_BOUNDARY_OBJECT_TYPE; 
	
	MetaDataOntology(ValueFactory factory) {
		this.factory = factory;
		
		// define classes
		ENTITY_CLASS = new Construct(genModelURI("entity"), "entity");
		KEY_CLASS = new Construct(genModelURI("Key"), "Key");
		FIELD_CLASS = new Construct(genModelURI("Field"), "Field");
		BOUNDARY_OBJECT_CLASS = new Construct(genModelURI("Boundary Object"), "Boundary Object");
		
		
		// define relationships
		HAS_CONTENT_ID = new Construct(genModelURI("hasContentId"), "hasContentId");
		HAS_KEY = new Construct(genModelURI("hasKey"), "hasKey");
		HAS_KEY_TYPE = new Construct(genModelURI("hasKeyType"), "hasKeyType");
		HAS_FIELD_DATA = new Construct(genModelURI("hasFieldData"), "hasFieldData");
		HAS_FIELD_TYPE = new Construct(genModelURI("hasFieldType"), "hasFieldType");
		HAS_FIELD_VALUE = new Construct(genModelURI("hasFieldValue"), "hasFieldValue");
		HAS_INDIRECT_RELATIONSHIP_TO = new Construct(genModelURI("hasIndirectRelationshipTo"), "hasIndirectRelationshipTo");
		HAS_DIRECT_RELATIONSHIP_TO = new Construct(genModelURI("hasDirectRelationshipTo"), "hasDirectRelationshipTo");
		HAS_BOUNDARY_OBJECT_TYPE = new Construct(genModelURI("hasBoundaryObjectType"), "hasBoundaryObjectType");
	}
	
	/** helper method to load all model triples into triple store using given connection */
	void loadModel(RepositoryConnection conn) throws RepositoryException {
		// add statements
		List<Statement> statements = new LinkedList<Statement>();
		
		// assert Classes
		statements.addAll(createClass(ENTITY_CLASS.URI, ENTITY_CLASS.LABEL));
		statements.addAll(createClass(KEY_CLASS.URI, KEY_CLASS.LABEL));
		statements.addAll(createClass(FIELD_CLASS.URI, FIELD_CLASS.LABEL));
		statements.addAll(createClass(BOUNDARY_OBJECT_CLASS.URI, BOUNDARY_OBJECT_CLASS.LABEL));
		
		// assert Predicates
		statements.addAll(createPredicate(HAS_CONTENT_ID.URI, HAS_CONTENT_ID.LABEL));
		statements.addAll(createPredicate(HAS_KEY.URI, HAS_KEY.LABEL));
		statements.addAll(createPredicate(HAS_KEY_TYPE.URI, HAS_KEY_TYPE.LABEL));
		statements.addAll(createPredicate(HAS_FIELD_DATA.URI, HAS_FIELD_DATA.LABEL));
		statements.addAll(createPredicate(HAS_FIELD_TYPE.URI, HAS_FIELD_TYPE.LABEL));
		statements.addAll(createPredicate(HAS_FIELD_VALUE.URI, HAS_FIELD_VALUE.LABEL));
		statements.addAll(createPredicate(HAS_INDIRECT_RELATIONSHIP_TO.URI, HAS_INDIRECT_RELATIONSHIP_TO.LABEL));
		statements.addAll(createPredicate(HAS_DIRECT_RELATIONSHIP_TO.URI, HAS_DIRECT_RELATIONSHIP_TO.LABEL));
		statements.addAll(createPredicate(HAS_BOUNDARY_OBJECT_TYPE.URI, HAS_BOUNDARY_OBJECT_TYPE.LABEL));
		
		conn.add(statements);
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
		statements.add(factory.createStatement(predicateUri, RDFS.LABEL, factory.createLiteral("label")));
		return statements;
	}
	
	private URI genModelURI(String specificPart){
		return factory.createURI(BASE_MODEL_URI + specificPart);
	}
	
	//simple way to group a things URI and label together
	public static class Construct {
		public final URI URI;
		public final String LABEL;
		
		public Construct(URI uri, String label) {
			this.URI = uri;
			this.LABEL = label;
		}
	}
}

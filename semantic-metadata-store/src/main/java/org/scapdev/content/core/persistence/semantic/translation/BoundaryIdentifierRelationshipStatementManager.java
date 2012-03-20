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
package org.scapdev.content.core.persistence.semantic.translation;

import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.scapdev.content.core.persistence.semantic.MetaDataOntology;
import org.scapdev.content.core.persistence.semantic.entity.EntityBuilder;

/**
 * <p>A manager to coordinate multiple IndirectRelationshipBuilders working in
 * parallel to build up all IndirectRelationships originating from a specific
 * entity.</p>
 * 
 * @see IndirectRelationshipBuilder
 */
class BoundaryIdentifierRelationshipStatementManager implements RegenerationStatementManager {
	private MetaDataOntology ontology;
	
	//all IDs of boundaryObjectRelationships
	private Collection<String> boundaryObjectRelationshipIds;
	
	// key = boundary_boject_id....this map is what this class builds
	private Map<String, BoundaryIdentifierRelationshipBuilder> boundaryIdentifierRelationships = new HashMap<String, BoundaryIdentifierRelationshipBuilder>();
	
	
	BoundaryIdentifierRelationshipStatementManager(MetaDataOntology ontology) {
		this.ontology = ontology;
		this.boundaryObjectRelationshipIds = ontology.getBoundaryIndentifierRelationshipIds();
	}
	
	/**
	 * Scans triple and processes it if it is relevant to boundaryObjectRelationships
	 * @param statement
	 * @return true if triple was processed in some way, false if it was just ignored.
	 */
	public boolean scan(Statement statement){
		URI predicate = statement.getPredicate();
		if (boundaryObjectRelationshipIds.contains(predicate.stringValue())){
			// a triple containing an boundary id relationship predicate found
			populateBoundaryObjectRelationshipInfo(ontology, statement);
			return true;
		}
		if (predicate.equals(ontology.HAS_BOUNDARY_OBJECT_TYPE.URI)){
			populateBoundaryObjectRelationshipExternalIdType(ontology, statement);
			return true;
		}
		if (predicate.equals(ontology.HAS_BOUNDARY_OBJECT_VALUE.URI)){
			populateBoundaryObjectRelationshipExternalIdValue(ontology, statement);
			return true;
		}
		return false;
	}
	
	/**
	 * <p>
	 * Called after all triples are processed to populate re-constituted entity
	 * with the IndirectRelationships found in the graph.
	 * </p>
	 * 
	 * @param entity
	 *            - to populate.
	 */
	public void populateEntity(EntityBuilder builder){
		for (BoundaryIdentifierRelationshipBuilder boundaryObjectRelBuilder : boundaryIdentifierRelationships.values()){
			IBoundaryIdentifierRelationship boundaryObjectRelBuilderRelationship = boundaryObjectRelBuilder.build(ontology, entity);
			builder.addRelationship(boundaryObjectRelBuilderRelationship);
		}
	}
	
	/**
	 * <p>
	 * Helper method to populate RelatioshipInfo based on the predicate found
	 * within the triple. Assumes Statement passed in contains a predicate that
	 * is a subProperty of HAS_BOUNDARY_OBJECT_RELATIONSHIP.
	 * </p>
	 * 
	 * @param model
	 * @param statement
	 * 
	 * @see RelationshipInfo
	 */
	private void populateBoundaryObjectRelationshipInfo(IMetadataModel model, Statement statement){
		// hit on an boundaryIdRelationship of some type
		String boundaryObjectRelationshipId = statement.getPredicate().stringValue();
		String boundaryObjectURI = statement.getObject().stringValue();
		BoundaryIdentifierRelationshipBuilder boundaryObjectRelBuilder = boundaryIdentifierRelationships.get(boundaryObjectURI);
		if (boundaryObjectRelBuilder == null){
			boundaryObjectRelBuilder = new BoundaryIdentifierRelationshipBuilder();
			boundaryObjectRelBuilder.setIndirectRelationshipInfo((IBoundaryIdentifierRelationshipDefinition)model.getRelationshipDefinitionById(boundaryObjectRelationshipId));
			boundaryIdentifierRelationships.put(boundaryObjectURI, boundaryObjectRelBuilder);
		} else {
			// can't be sure this was set before
			boundaryObjectRelBuilder.setIndirectRelationshipInfo((IBoundaryIdentifierRelationshipDefinition)model.getRelationshipDefinitionById(boundaryObjectRelationshipId));
		}
	}
	
	/**
	 * <p>
	 * Helper method to populate ExternalIdType based on the provided triple .
	 * Assumes Statement passed in contains a HAS_BOUNDARY_OBJECT_TYPE predicate.
	 * </p>
	 * 
	 * @param model
	 * @param statement
	 * 
	 */
	private void populateBoundaryObjectRelationshipExternalIdType(IMetadataModel model, Statement statement){
		String boundaryObjectURI = statement.getSubject().stringValue();
		String boundaryObjectType = statement.getObject().stringValue();
		BoundaryIdentifierRelationshipBuilder boundaryObjectRelBuilder = boundaryIdentifierRelationships.get(boundaryObjectURI);
		if (boundaryObjectRelBuilder == null){
			boundaryObjectRelBuilder = new BoundaryIdentifierRelationshipBuilder();
			boundaryObjectRelBuilder.setExternalIdType(boundaryObjectType);
			boundaryIdentifierRelationships.put(boundaryObjectURI, boundaryObjectRelBuilder);
		} else {
			// can't be sure this was set before
			boundaryObjectRelBuilder.setExternalIdType(boundaryObjectType);
		}
	}
	
	/**
	 * <p>
	 * Helper method to populate ExternalIdValue based on the provided triple .
	 * Assumes Statement passed in contains a HAS_BOUNDARY_OBJECT_VALUE predicate.
	 * </p>
	 * 
	 * @param model
	 * @param statement
	 * 
	 */
	private void populateBoundaryObjectRelationshipExternalIdValue(IMetadataModel model, Statement statement){
		String boundaryObjectURI = statement.getSubject().stringValue();
		String boundaryObjectValue = statement.getObject().stringValue();
		BoundaryIdentifierRelationshipBuilder boundaryObjectRelBuilder = boundaryIdentifierRelationships.get(boundaryObjectURI);
		if (boundaryObjectRelBuilder == null){
			boundaryObjectRelBuilder = new BoundaryIdentifierRelationshipBuilder();
			boundaryObjectRelBuilder.setExternalIdValue(boundaryObjectValue);
			boundaryIdentifierRelationships.put(boundaryObjectURI, boundaryObjectRelBuilder);
		} else {
			// can't be sure this was set before
			boundaryObjectRelBuilder.setExternalIdValue(boundaryObjectValue);
		}
	}
	
}

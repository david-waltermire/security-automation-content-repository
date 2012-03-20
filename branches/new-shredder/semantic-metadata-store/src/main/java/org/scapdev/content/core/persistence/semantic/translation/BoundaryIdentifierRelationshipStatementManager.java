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
 * <p>A manager to coordinate multiple BoundaryIdentiferRelationshipBuilders working in
 * parallel to build up all BoundaryIdentiferRelationships originating from a specific
 * entity.</p>
 * 
 * @see BoundaryIdentiferRelationshipBuilder
 */
class BoundaryIdentifierRelationshipStatementManager implements RegenerationStatementManager {
	private MetaDataOntology ontology;
	
	//all IDs of boundaryIdentifierRelationships
	private Collection<String> boundaryIdentifierRelationshipIds;
	
	// key = boundary_boject_id....this map is what this class builds
	private Map<String, BoundaryIdentifierRelationshipBuilder> boundaryIdentifierRelationships = new HashMap<String, BoundaryIdentifierRelationshipBuilder>();
	
	
	BoundaryIdentifierRelationshipStatementManager(MetaDataOntology ontology) {
		this.ontology = ontology;
		this.boundaryIdentifierRelationshipIds = ontology.getBoundaryIndentifierRelationshipIds();
	}
	
	/**
	 * Scans triple and processes it if it is relevant to boundaryIdentifierRelationships
	 * @param statement
	 * @return true if triple was processed in some way, false if it was just ignored.
	 */
	public boolean scan(Statement statement){
		URI predicate = statement.getPredicate();
		if (boundaryIdentifierRelationshipIds.contains(predicate.stringValue())){
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
	 * with the BoundaryIdentiferRelationships found in the graph.
	 * </p>
	 * 
	 * @param entity
	 *            - to populate.
	 */
	public void populateEntity(EntityBuilder builder){
		for (BoundaryIdentifierRelationshipBuilder boundaryIdentifierRelBuilder : boundaryIdentifierRelationships.values()){
			IBoundaryIdentifierRelationship boundaryIdentifierRelBuilderRelationship = boundaryIdentifierRelBuilder.build(ontology);
			builder.addRelationship(boundaryIdentifierRelBuilderRelationship);
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
		String boundaryIdentifierRelationshipId = statement.getPredicate().stringValue();
		String boundaryIdentifierURI = statement.getObject().stringValue();
		BoundaryIdentifierRelationshipBuilder boundaryIdentifierRelBuilder = boundaryIdentifierRelationships.get(boundaryIdentifierURI);
		if (boundaryIdentifierRelBuilder == null){
			boundaryIdentifierRelBuilder = new BoundaryIdentifierRelationshipBuilder();
			boundaryIdentifierRelBuilder.setBoundaryIdentiferRelationshipInfo((IBoundaryIdentifierRelationshipDefinition)model.getRelationshipDefinitionById(boundaryIdentifierRelationshipId));
			boundaryIdentifierRelationships.put(boundaryIdentifierURI, boundaryIdentifierRelBuilder);
		} else {
			// can't be sure this was set before
			boundaryIdentifierRelBuilder.setBoundaryIdentiferRelationshipInfo((IBoundaryIdentifierRelationshipDefinition)model.getRelationshipDefinitionById(boundaryIdentifierRelationshipId));
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
		String boundaryIdentifierURI = statement.getSubject().stringValue();
		String boundaryIdentifierType = statement.getObject().stringValue();
		BoundaryIdentifierRelationshipBuilder boundaryIdentifierRelBuilder = boundaryIdentifierRelationships.get(boundaryIdentifierURI);
		if (boundaryIdentifierRelBuilder == null){
			boundaryIdentifierRelBuilder = new BoundaryIdentifierRelationshipBuilder();
			boundaryIdentifierRelBuilder.setExternalIdType(boundaryIdentifierType);
			boundaryIdentifierRelationships.put(boundaryIdentifierURI, boundaryIdentifierRelBuilder);
		} else {
			// can't be sure this was set before
			boundaryIdentifierRelBuilder.setExternalIdType(boundaryIdentifierType);
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
		String boundaryIdentifierURI = statement.getSubject().stringValue();
		String boundaryIdentifierValue = statement.getObject().stringValue();
		BoundaryIdentifierRelationshipBuilder boundaryIdentifierRelBuilder = boundaryIdentifierRelationships.get(boundaryIdentifierURI);
		if (boundaryIdentifierRelBuilder == null){
			boundaryIdentifierRelBuilder = new BoundaryIdentifierRelationshipBuilder();
			boundaryIdentifierRelBuilder.setExternalIdValue(boundaryIdentifierValue);
			boundaryIdentifierRelationships.put(boundaryIdentifierURI, boundaryIdentifierRelBuilder);
		} else {
			// can't be sure this was set before
			boundaryIdentifierRelBuilder.setExternalIdValue(boundaryIdentifierValue);
		}
	}
	
}

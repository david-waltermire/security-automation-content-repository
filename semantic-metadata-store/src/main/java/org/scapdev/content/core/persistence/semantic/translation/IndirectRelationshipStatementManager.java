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
import gov.nist.scap.content.model.IMutableEntity;
import gov.nist.scap.content.model.definitions.IBoundaryIdentifierRelationshipDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.scapdev.content.core.persistence.semantic.MetaDataOntology;

/**
 * <p>A manager to coordinate multiple IndirectRelationshipBuilders working in
 * parallel to build up all IndirectRelationships originating from a specific
 * entity.</p>
 * 
 * @see IndirectRelationshipBuilder
 */
class IndirectRelationshipStatementManager implements RegenerationStatementManager {
	private MetaDataOntology ontology;
	
	//should be okay to store at class level since this should be created/destroyed within a single instance
	private IMetadataModel model;
	
	//all IDs of indirectRelationships
	private Collection<String> indirectRelationshipIds;
	
	// key = boundary_boject_id....this map is what this class builds
	private Map<String, IndirectRelationshipBuilder> indirectRelationships = new HashMap<String, IndirectRelationshipBuilder>();
	
	
	IndirectRelationshipStatementManager(MetaDataOntology ontology, IMetadataModel model) {
		this.ontology = ontology;
		this.model = model;
		this.indirectRelationshipIds = model.getIndirectRelationshipIds();
	}
	
	/**
	 * Scans triple and processes it if it is relevant to indirectRelationships
	 * @param statement
	 * @return true if triple was processed in some way, false if it was just ignored.
	 */
	public boolean scan(Statement statement){
		URI predicate = statement.getPredicate();
		if (indirectRelationshipIds.contains(predicate.stringValue())){
			// a triple containing an indirect relationship predicate found
			populateIndirectRelationshipInfo(model, statement);
			return true;
		}
		if (predicate.equals(ontology.HAS_BOUNDARY_OBJECT_TYPE.URI)){
			populateIndirectRelationshipExternalIdType(model, statement);
			return true;
		}
		if (predicate.equals(ontology.HAS_BOUNDARY_OBJECT_VALUE.URI)){
			populateIndirectRelationshipExternalIdValue(model, statement);
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
	public void populateEntity(IMutableEntity<?> entity){
		for (IndirectRelationshipBuilder indirectRelBuilder : indirectRelationships.values()){
			IBoundaryIdentifierRelationship indirectRelationship = indirectRelBuilder.build(model, entity);
			entity.addRelationship(indirectRelationship);
		}
	}
	
	/**
	 * <p>
	 * Helper method to populate RelatioshipInfo based on the predicate found
	 * within the triple. Assumes Statement passed in contains a predicate that
	 * is a subProperty of HAS_INDIRECT_RELATIONSHIP.
	 * </p>
	 * 
	 * @param model
	 * @param statement
	 * 
	 * @see RelationshipInfo
	 */
	private void populateIndirectRelationshipInfo(IMetadataModel model, Statement statement){
		// hit on an indirectRelationship of some type
		String indirectRelationshipId = statement.getPredicate().stringValue();
		String boundaryObjectURI = statement.getObject().stringValue();
		IndirectRelationshipBuilder indirectRelBuilder = indirectRelationships.get(boundaryObjectURI);
		if (indirectRelBuilder == null){
			indirectRelBuilder = new IndirectRelationshipBuilder();
			indirectRelBuilder.setIndirectRelationshipInfo((IBoundaryIdentifierRelationshipDefinition)model.getRelationshipInfoById(indirectRelationshipId));
			indirectRelationships.put(boundaryObjectURI, indirectRelBuilder);
		} else {
			// can't be sure this was set before
			indirectRelBuilder.setIndirectRelationshipInfo((IBoundaryIdentifierRelationshipDefinition)model.getRelationshipInfoById(indirectRelationshipId));
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
	private void populateIndirectRelationshipExternalIdType(IMetadataModel model, Statement statement){
		String boundaryObjectURI = statement.getSubject().stringValue();
		String boundaryObjectType = statement.getObject().stringValue();
		IndirectRelationshipBuilder indirectRelBuilder = indirectRelationships.get(boundaryObjectURI);
		if (indirectRelBuilder == null){
			indirectRelBuilder = new IndirectRelationshipBuilder();
			indirectRelBuilder.setExternalIdType(boundaryObjectType);
			indirectRelationships.put(boundaryObjectURI, indirectRelBuilder);
		} else {
			// can't be sure this was set before
			indirectRelBuilder.setExternalIdType(boundaryObjectType);
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
	private void populateIndirectRelationshipExternalIdValue(IMetadataModel model, Statement statement){
		String boundaryObjectURI = statement.getSubject().stringValue();
		String boundaryObjectValue = statement.getObject().stringValue();
		IndirectRelationshipBuilder indirectRelBuilder = indirectRelationships.get(boundaryObjectURI);
		if (indirectRelBuilder == null){
			indirectRelBuilder = new IndirectRelationshipBuilder();
			indirectRelBuilder.setExternalIdValue(boundaryObjectValue);
			indirectRelationships.put(boundaryObjectURI, indirectRelBuilder);
		} else {
			// can't be sure this was set before
			indirectRelBuilder.setExternalIdValue(boundaryObjectValue);
		}
	}
	
}

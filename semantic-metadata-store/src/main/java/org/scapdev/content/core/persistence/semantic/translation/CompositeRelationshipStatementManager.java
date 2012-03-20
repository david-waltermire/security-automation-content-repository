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

import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.definitions.ICompositeRelationshipDefinition;
import gov.nist.scap.content.model.definitions.collection.IMetadataModel;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.scapdev.content.core.persistence.semantic.MetaDataOntology;
import org.scapdev.content.core.persistence.semantic.entity.EntityBuilder;
import org.scapdev.content.core.persistence.semantic.entity.EntityProxy;

/**
 * <p>A manager to build up composite relationships</p>
 * 
 * @see CompositeRelationshipBuilder
 */
class CompositeRelationshipStatementManager implements RegenerationStatementManager {
	private MetaDataOntology ontology;
	
	//all IDs of compositeIdentifierRelationships
	private Collection<String> compositeRelationshipIds;
	
	// key = boundary_boject_id....this map is what this class builds
	private Map<String, CompositeRelationshipBuilder> compositeRelationships = new HashMap<String, CompositeRelationshipBuilder>();
	
	
	CompositeRelationshipStatementManager(MetaDataOntology ontology) {
		this.ontology = ontology;
		this.compositeRelationshipIds = ontology.getCompositeRelationshipIds();
	}
	
	/**
	 * Scans triple and processes it if it is relevant to compositeRelationships
	 * @param statement
	 * @return true if triple was processed in some way, false if it was just ignored.
	 */
	public boolean scan(Statement statement){
		URI predicate = statement.getPredicate();
		if (compositeRelationshipIds.contains(predicate.stringValue())){
			populateCompositeRelationshipInfo(ontology, statement);
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
		for (CompositeRelationshipBuilder compositeRelBuilder : compositeRelationships.values()){
			ICompositeRelationship compositeRelBuilderRelationship = compositeRelBuilder.build(ontology);
			builder.addRelationship(compositeRelBuilderRelationship);
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
	private void populateCompositeRelationshipInfo(IMetadataModel model, Statement statement){
		// hit on an boundaryIdRelationship of some type
		String compositeRelationshipId = statement.getPredicate().stringValue();
		String compositeURI = statement.getObject().stringValue();
		CompositeRelationshipBuilder compositeRelBuilder = compositeRelationships.get(compositeURI);
		if (compositeRelBuilder == null){
			compositeRelBuilder = new CompositeRelationshipBuilder();
			compositeRelBuilder.setCompositeRelationshipInfo((ICompositeRelationshipDefinition)model.getRelationshipDefinitionById(compositeRelationshipId));
			compositeRelBuilder.setRelatedEntity(new EntityProxy());
			compositeRelationships.put(compositeURI, compositeRelBuilder);
		}
	}
	
}

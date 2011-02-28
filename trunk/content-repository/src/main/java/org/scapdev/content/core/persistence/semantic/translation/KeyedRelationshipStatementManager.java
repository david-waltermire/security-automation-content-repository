/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 paul
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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.scapdev.content.core.persistence.semantic.MetaDataOntology;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.KeyedRelationshipInfo;
import org.scapdev.content.model.MetadataModel;
import org.scapdev.content.model.RelationshipInfo;

class KeyedRelationshipStatementManager implements
		RegenerationStatementManager {
	private MetaDataOntology ontology;
	
	//should be okay to store at class level since this should be created/destroyed within a single instance
	private MetadataModel model;
	
	//all IDs of directRelationships
	private Collection<String> directRelationshipIds;
	
	// key = relatedEntity_id....this map is what this class builds
	private Map<String, KeyedRelationshipBuilder> keyedRelationships = new HashMap<String, KeyedRelationshipBuilder>();
	
	KeyedRelationshipStatementManager(MetaDataOntology ontology, MetadataModel model) {
		this.ontology = ontology;
		this.model = model;
		this.directRelationshipIds = model.getKeyedRelationshipIds();
	}
	
	@Override
	public boolean scan(Statement statement) {
		URI predicate = statement.getPredicate();
		if (directRelationshipIds.contains(predicate.stringValue())){
			// a triple containing an indirect relationship predicate found
			populateKeyedRelationshipInfo(model, statement);
			return true;
		}
		return false;
	}
	
	@Override
	public void populateEntity(RebuiltEntity entity) {
		for (KeyedRelationshipBuilder keyedRelBuilder : keyedRelationships.values()){
			KeyedRelationship keyedRelationship = keyedRelBuilder.build(model, entity);
			entity.addKeyedRelationship(keyedRelationship);
		}
	}

	/**
	 * <p>
	 * Helper method to populate RelatioshipInfo based on the predicate found
	 * within the triple. Assumes Statement passed in contains a predicate that
	 * is a subProperty of HAS_DIRECT_RELATIONSHIP.
	 * </p>
	 * 
	 * @param model
	 * @param statement
	 * 
	 * @see RelationshipInfo
	 */
	private void populateKeyedRelationshipInfo(MetadataModel model2,
			Statement statement) {
		// hit on an keyedRelationship (called directRelationship in ontology) of some type
		String keyedRelationshipId = statement.getPredicate().stringValue();
		String relatedEntityURI = statement.getObject().stringValue();
		KeyedRelationshipBuilder keyedRelBuilder = keyedRelationships.get(relatedEntityURI);
		if (keyedRelBuilder == null){
			keyedRelBuilder = new KeyedRelationshipBuilder();
			keyedRelBuilder.setKeyedRelationshipInfo((KeyedRelationshipInfo)model.getRelationshipById(keyedRelationshipId));
			keyedRelationships.put(relatedEntityURI, keyedRelBuilder);
		} else {
			// can't be sure this was set before
			keyedRelBuilder.setKeyedRelationshipInfo((KeyedRelationshipInfo)model.getRelationshipById(keyedRelationshipId));
		}
		
	}



}

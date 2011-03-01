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

import org.scapdev.content.core.persistence.semantic.MetaDataOntology;
import org.scapdev.content.model.AbstractRelationship;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.Key;
import org.scapdev.content.model.KeyedRelationship;
import org.scapdev.content.model.KeyedRelationshipInfo;
import org.scapdev.content.model.MetadataModel;

//TODO: need to handle rebuilding the KEY AND ENTITY!!!!
class KeyedRelationshipBuilder {
	private KeyedRelationshipInfo keyedRelationshipInfo;
	
	private Key relatedEntityKey;
	
	
	KeyedRelationshipBuilder(MetaDataOntology ontology) {
	}
	
	void setKeyedRelationshipInfo(KeyedRelationshipInfo keyedRelationshipInfo) {
		this.keyedRelationshipInfo = keyedRelationshipInfo;
	}
	
	void setRelatedEntityKey(Key relatedEntityKey) {
		this.relatedEntityKey = relatedEntityKey;
	}


	
	
	/**
	 * Will build an instance of an keyed relationship. NOTE: the client must
	 * add the newly created relationship to the owning entity directly after
	 * calling this method.
	 * 
	 * @param model
	 * @param entity
	 *            - the owningEntity of the relationship
	 * @return
	 */
	KeyedRelationship build(MetadataModel model, RebuiltEntity entity){
		if (keyedRelationshipInfo == null || relatedEntityKey == null){
			throw new IncompleteBuildStateException("Not all values are populated");
		}
		KeyedRelationship rel = new InternalKeyedRelationship(keyedRelationshipInfo, entity, relatedEntityKey);
		
		return rel;
	}
	
	private static class InternalKeyedRelationship extends
			AbstractRelationship<KeyedRelationshipInfo> implements
			KeyedRelationship {
		//key of related entity
		private Key key;
		
		InternalKeyedRelationship(KeyedRelationshipInfo relationshipInfo,
				Entity owningEntity, Key key) {
			super(relationshipInfo, owningEntity);
			this.key = key;
		}

		@Override
		public Key getKey() {
			return key;
		}

		
	}
}

/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 David Waltermire
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
package org.scapdev.content.model;

import java.util.Collection;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.scapdev.jaxb.reflection.model.JAXBModel;

public interface MetadataModel {
	Map<String, String> getNamespaceToPrefixMap();
	JAXBModel getModel();
	JAXBContext getJAXBContext();
	RelationshipInfo getRelationshipByKeyRefId(String id);
	EntityInfo getEntityById(String id);
	RelationshipInfo getRelationshipById(String id);
	EntityInfo getEntityByKeyId(String idRef);
	ExternalIdentifierInfo getExternalIdentifierById(String externalIdentifierId);
	/**
	 * Based on the metamodel definition of entity-identifier-mapping this
	 * method must find a pattern that matches the provided identifier and build
	 * the associated key.
	 * @param string the identifier
	 * @return a key corrisponding to the identifier or <code>null</code> if
	 * 		there is no match
	 */
	Key getKeyFromMappedIdentifier(String identifier);
	
	/**
	 * Return a collection of all IndirectRelationship Ids.
	 */
	Collection<String> getIndirectRelationshipIds();
	
	/**
	 * Return a collection of all KeyedRelationship Ids.
	 */
	Collection<String> getKeyedRelationshipIds();
	
}

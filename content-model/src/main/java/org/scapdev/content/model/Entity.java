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

import javax.xml.bind.JAXBElement;

/**
 * Represents an instance of an identifiable XML node that may be a document
 * instance or a standalone fragment of a larger document.
 * @see EntityInfo
 */
public interface Entity {
	/**
	 * Retrieves information about the entity type for this instance defined
	 * within a schema model.
	 * @return the entityInfo
	 */
	EntityInfo getEntityInfo();
	/**
	 * Retrieves the key that identifies this XML node
	 * @return a key
	 */
	Key getKey();
	/**
	 * Retrieves the JAXB data associated with this instance.
	 * @return the object
	 */
	JAXBElement<Object> getObject();
	/**
	 * This method retrieves the combination of instances returned by
	 * {@link #getKeyedRelationships} and {@link #getIndirectRelationships}.  It
	 * can be used to retrieve all relationships associated with this entity. 
	 * @return a collection of relationships
	 */
	Collection<Relationship> getRelationships();
	/**
	 * This method retrieves all keyed relationships associated with this entity.
	 * @return a collection of relationships
	 */
	Collection<? extends KeyedRelationship> getKeyedRelationships();
	/**
	 * This method retrieves all indirect relationships associated with this entity.
	 * @return a collection of relationships
	 */
	Collection<IndirectRelationship> getIndirectRelationships();
}

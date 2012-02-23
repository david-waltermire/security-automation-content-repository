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

/**
 * Represents definition of an relationship type within the XML schema meta
 * model that is described by a key reference that is directly associated with
 * an {@link Entity}.
 * 
 * @see AbstractKeyedRelationship
 * @see org.scapdev.content.annotation.KeyRef
 */
public interface KeyedRelationshipInfo extends RelationshipInfo {
	/**
	 * Retrieves the key reference type associated with this relationship
	 * definition.
	 * 
	 * @return the key reference information associated with this field
	 *         reference type
	 */
	KeyRefInfo getKeyRefInfo();

	/**
	 * Generates a new key based on the XML bound node provided by the
	 * <code>instance</code> parameter.
	 * 
	 * @param instance an XML bound node
	 * @return a new Key instance
	 * @throws NullFieldValueException if one of the fields was not provided or
	 *             <code>null</code>
	 * @throws KeyException if the key is malformed or incomplete
	 * @throws ModelInstanceException if the XML bound node is malformed or
	 *             incomplete
	 */
	Key newKey(Object instance) throws NullFieldValueException, KeyException,
			ModelInstanceException;

	/**
	 * {@inheritDoc}
	 * 
	 * @param instance an XML bound node
	 * @param owningEntity the entity containing the relationship
	 * @return an unordered collection of keyed relationships
	 */
	Collection<? extends KeyedRelationship> newRelationships(Object instance,
			Entity owningEntity);
}

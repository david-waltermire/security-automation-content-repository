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
 * Represents definition of an indirect relationship type within the XML schema
 * meta model that is directly associated with an {@link Entity}.
 * 
 * @see IndirectRelationship
 * @see org.scapdev.content.model.jaxb.IndirectRelationshipType
 * @see org.scapdev.content.annotation.Indirect
 */
public interface IndirectRelationshipInfo extends RelationshipInfo {
	/**
	 * {@inheritDoc}
	 * 
	 * @param instance the XML bound object that holds one or more indirect
	 *            relationships.
	 * @param owningEntity the Entity instance that contains the new
	 *            relationships
	 * @return an unordered collection of indirect relationships that appear in the XML bound
	 *         object graph at the current instance
	 */
	Collection<IndirectRelationship> newRelationships(Object instance,
			Entity owningEntity);
}

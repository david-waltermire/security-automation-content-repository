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

import org.scapdev.jaxb.reflection.model.JAXBClass;

/**
 * Represents the definition of a relationship type within the XML schema meta model
 * that is directly associated with an {@link Entity}.
 * 
 * @see Relationship
 * @see org.scapdev.content.model.jaxb.RelationshipType
 */
public interface RelationshipInfo extends SchemaComponent {
	/**
	 * Retrieves the XML bound type that contains the relationship
	 * 
	 * @return a JAXB bound type
	 */
	JAXBClass getOwningJAXBClass();

	/**
	 * Generates an unordered collection of new relationships based on the XML
	 * node indicated by the <code>instance</code>. A collection is necessary to
	 * allow for a series of references to be captured within a single XML
	 * element or property.
	 * 
	 * @param instance an XML bound node
	 * @param owningEntity the entity containing the relationship
	 * @return an unordered collection of relationships
	 */
	Collection<? extends Relationship> newRelationships(Object instance,
			Entity owningEntity);
}

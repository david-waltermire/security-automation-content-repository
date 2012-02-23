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

import org.scapdev.content.model.jaxb.KeyType;

/**
 * Represents a key type within the XML schema meta model. A key type has a
 * collection of identification field types that form a compound index. A
 * {@link Key} is an instance of this type.
 * 
 * @see Key
 * @see KeyType
 * @see org.scapdev.content.annotation.Key
 */
public interface KeyInfo extends Component {
	/**
	 * Retrieves the entity type information associated with this key type.
	 * 
	 * @return the entity info associated with this key type.
	 */
	EntityInfo getEntityInfo();

	/**
	 * Retrieves an unordered collection of field type information associated
	 * with this key type.
	 * 
	 * @return the field information associated with this key type.
	 */
	Collection<FieldInfo> getFieldInfos();

	/**
	 * Retrieves the field type that is identified by the <code>id</code>
	 * 
	 * @param id the identifier of the field type to retrieve
	 * @return the identified field information associated with this key type or
	 *         <code>null</code> if the entry doesn't exist
	 * @see FieldInfo#getId()
	 */
	FieldInfo getFieldInfo(String id);

	/**
	 * Generates a new key based on the XML bound node provided by the
	 * <code>instance</code> parameter.
	 * 
	 * @param instance an XML bound node
	 * @return a new Key instance
	 * @throws NullFieldValueException if one of the fields was not provided or <code>null</code>
	 * @throws KeyException if the key is malformed or incomplete
	 * @throws ModelInstanceException if the XML bound node is malformed or incomplete
	 */
	Key newKey(Object instance) throws NullFieldValueException, KeyException, ModelInstanceException;
}

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
package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.KeyException;

import org.apache.xmlbeans.XmlCursor;

/**
 * Represents a key reference within an XML model.
 */
public interface IKeyRefDefinition extends IDefinition {
	/**
	 * Retrieves the key definition associated with this reference
	 * 
	 * @return the key definition associated with this reference
	 */
	IKeyDefinition getKeyDefinition();

	/**
	 * Generates a new key based on the XML bound node provided by the
	 * <code>instance</code> parameter.
	 * 
	 * @param parentContext the parent entity containing this reference
	 * @param cursor the current location in the XML instance used to resolve
	 * 		the reference
	 * @return a new Key instance that can be used to lookup the referenced
	 * 		entity
	 * @throws KeyException if the key is malformed or incomplete
	 * @throws ContentException 
	 */
	IKey getKey(IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ContentException;
}

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

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;

/**
 * Provides common functionality for all translators.
 *
 */
public abstract class AbstractSemanticTranslator {
	
	private final String baseURI;
	
	protected final ValueFactory factory;
	
	/**
	 * 
	 * @param baseURI - the base URI to use for all RDF individuals produced by this translator
	 * @param factory
	 */
	public AbstractSemanticTranslator(String baseURI, ValueFactory factory) {
		this.baseURI = baseURI;
		this.factory = factory;
	}
	
	protected final URI genInstanceURI(String specificPart){
		return factory.createURI(baseURI + specificPart);
	}
	

	

}

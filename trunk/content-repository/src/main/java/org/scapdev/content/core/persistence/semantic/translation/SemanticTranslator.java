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

import java.util.List;

import org.openrdf.model.Statement;
import org.scapdev.content.core.persistence.hybrid.ContentRetrieverFactory;
import org.scapdev.content.model.MetadataModel;


/**
 * <p>Translate back and forth between Java MetaModel instances and their RDF equivalents.</p>
 *
 *	TODO: need to remove all translation concepts...just serve to tie us to hard-coded semantics...not model driven
 *
 *	@param T - the metaModel type
 */
public interface SemanticTranslator<T> {

	/**
	 * Translate from RDF graph to java instance of type T. List of statements
	 * may contain more than the statements required to create instance of T.
	 * 
	 * @param statements
	 *            - rdf graph
	 * @return
	 */
	T translateToJava(List<Statement> statements, MetadataModel model, ContentRetrieverFactory contentRetrieverFactory);
	
	/**
	 * Translate from java instance of type T to RDF graph representing instance.
	 * @param instance
	 * @param contentId - id to associate with content
	 * @return 
	 */
	List<Statement> translateToRdf(T instance, String contentId);
}

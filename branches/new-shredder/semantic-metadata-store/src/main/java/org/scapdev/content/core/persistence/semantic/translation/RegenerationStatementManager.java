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

import gov.nist.scap.content.model.IMutableEntity;

import org.openrdf.model.Statement;

/**
 * 
 * <p>
 * Manages all triples associated with a given type, with the objective of
 * both processing those triples, and eventually rebuilding an instance of the type out
 * of them.
 * </p>
 * 
 * 
 * @see MetadataModel
 */
interface RegenerationStatementManager {
	/**
	 * Scans triple and processes it if it is relevant to indirectRelationships
	 * 
	 * @param statement
	 * @return true if triple was processed in some way, false if it was just
	 *         ignored.
	 */
	boolean scan(Statement statement);
	
	/**
	 * <p>
	 * Called after all triples are processed to populate re-constituted entity
	 * with the built instance (or instances depending on relationship) of type
	 * found in the graph.
	 * </p>
	 * 
	 * @param entity
	 *            - to populate.
	 */
	void populateEntity(IMutableEntity<?> entity);
	
	
}

/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 paul
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
package gov.nist.scap.content.semantic.translation;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.semantic.MetaDataOntology;
import gov.nist.scap.content.semantic.managers.KeyStatementManager;

import java.util.Set;

import org.openrdf.model.Statement;

/**
 * Used to produce a key from a set of entity statements
 * @author Adam Halbardier
 *
 */
public class KeyTranslator {
	private MetaDataOntology ontology;
	
	/**
	 * default constructor
	 * @param ontology the metadata ontology
	 */
	public KeyTranslator(MetaDataOntology ontology) {
		this.ontology = ontology;
	}
	
	/**
	 * translate a set of statements into a Key
	 * @param statements RDF statements representing at least a single key (may be more, but duplicate statements should not exist)
	 * @return the generated key
	 */
	public IKey translateToJava(Set<Statement> statements) {
		KeyStatementManager statementManager = new KeyStatementManager(ontology);
		
		for (Statement statement : statements){
			if (statementManager.scan(statement)){
				continue;
			}
		}
		
		return statementManager.produceKey();
	}
	
}

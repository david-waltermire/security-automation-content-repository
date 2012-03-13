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

import gov.nist.scap.content.model.IKey;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

/**
 * <p>
 * Represents a partial graph of an entity. Useful for passing to another class
 * that will be able to complete the graph. Assumes only HAS_DIRECT_RELATIONSHIP
 * predicates are incommlete.
 * </p>
 * 
 * @See {@link IEntity}
 * 
 *      TODO: this class is way to specific, see if you can make the concept
 *      more generic.
 * 
 */
public class PartialEntityGraph {
	private List<Statement> completeStatements;
	
	private List<IncompleteStatement> incompleteStatements;
	
	PartialEntityGraph() {
		incompleteStatements = new LinkedList<IncompleteStatement>();
		completeStatements = new LinkedList<Statement>();
	}
	
	void setCompleteStatements() {
	}
	
	/**
	 * Add a complete triple
	 * @param statement
	 */
	void add(Statement statement){
		completeStatements.add(statement);
	}
	
	/**
	 * add an incomplete triple to be handled later
	 * @param subject
	 * @param predicate
	 * @param relatedEntityKey
	 */
	void add(URI subject, URI predicate, IKey relatedEntityKey){
		incompleteStatements.add(new IncompleteStatement(subject, predicate, relatedEntityKey));
	}
	
	/**
	 * Get the complete statements that are ready for adding to triple store
	 * @return
	 */
	public List<Statement> getCompleteStatements() {
		return completeStatements;
	}
	
	/**
	 * Get the statements that need to be completed (ie. connected to related entity).
	 * @return
	 */
	public List<IncompleteStatement> getIncompleteStatements() {
		return incompleteStatements;
	}
	
	
	public static class IncompleteStatement{
		private final URI subject;
		private final URI predicate;
		private final IKey relatedEntityKey;
		
		IncompleteStatement(URI subject, URI predicate, IKey relatedEntityKey) {
			this.subject = subject;
			this.predicate = predicate;
			this.relatedEntityKey = relatedEntityKey;
		}
		
		public URI getPredicate() {
			return predicate;
		}
		
		public IKey getRelatedEntityKey() {
			return relatedEntityKey;
		}
		
		public URI getSubject() {
			return subject;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj){
				return true;
			}
			if (obj instanceof IncompleteStatement){
				IncompleteStatement that = (IncompleteStatement)obj;
				return this.subject.equals(that.subject) && this.predicate.equals(that.predicate) && this.relatedEntityKey.equals(relatedEntityKey);
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			int result = 1;
			result = 37 * result + subject.hashCode();
			result = 37 * result + predicate.hashCode();
			result = 37 * result + relatedEntityKey.hashCode();
			return result;
		}
		
		@Override
		public String toString() {
			StringBuilder b = new StringBuilder();
			b.append(subject.stringValue()).append(" ").append(predicate.stringValue()).append(" ").append(relatedEntityKey.toString());
			return b.toString();
		}
	}
	
}

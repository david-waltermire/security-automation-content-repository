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
package gov.nist.scap.content.semantic.managers;

import gov.nist.scap.content.model.definitions.collection.IMetadataModel;
import gov.nist.scap.content.semantic.IPersistenceContext;
import gov.nist.scap.content.semantic.entity.EntityBuilder;

import java.util.HashSet;
import java.util.Set;

import org.openrdf.model.Statement;
import org.openrdf.model.URI;

public class VersionStatementManager implements RegenerationStatementManager {
    private final IPersistenceContext persistContext;
	
    final private Set<URI> versionStatements;
    private String versionValue;
    
    public VersionStatementManager(IPersistenceContext persistContext) {
	    this.persistContext = persistContext;
	    final IPersistenceContext pc = persistContext;
	    versionStatements = new HashSet<URI>() {
            private static final long serialVersionUID = -5931621244275620673L;

            {
	               add(pc.getOntology().HAS_VERSION.URI);
	               add(pc.getOntology().HAS_VERSION_TYPE.URI);
	               add(pc.getOntology().HAS_VERSION_VALUE.URI);
	        }
	    };

	}
	
	@Override
	public boolean scan(Statement statement){
		if (versionStatements.contains(statement.getPredicate())){
			populateVersionInfo(persistContext.getOntology(), statement);
			return true;
		}
		return false;
	}
	
	public void populateEntity(EntityBuilder builder){
	    builder.setVersionValue(versionValue);
	}
	
    private void populateVersionInfo(IMetadataModel model2,
            Statement statement) {
        String type = statement.getPredicate().stringValue();
        if( persistContext.getOntology().HAS_VERSION_VALUE.URI.stringValue().equals(type) ) {
            //there's only ever 1 version
            //TODO does version type need to be read from the triple store?
            versionValue = statement.getObject().stringValue();
        }
    }
}

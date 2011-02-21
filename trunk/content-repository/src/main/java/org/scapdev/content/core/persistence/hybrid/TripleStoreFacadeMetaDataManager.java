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
package org.scapdev.content.core.persistence.hybrid;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.openrdf.repository.Repository;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.sail.memory.MemoryStore;
import org.scapdev.content.model.Entity;
import org.scapdev.content.model.ExternalIdentifierInfo;
import org.scapdev.content.model.Key;

/**
 * At this point this is just going to be a facade into the triple store REST interfaces
 *
 */
public class TripleStoreFacadeMetaDataManager implements MetadataStore {
	private Repository repository;
	
	public TripleStoreFacadeMetaDataManager() {
		//NOTE: this type is non-inferencing, see http://www.openrdf.org/doc/sesame2/2.3.2/users/ch08.html for more detail
		repository = new SailRepository(new MemoryStore());
	}
	
	@Override
	public EntityDescriptor getEntityDescriptor(Key key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EntityDescriptor> getEntityDescriptor(
			ExternalIdentifierInfo externalIdentifierInfo, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void persist(Entity entity, String contentId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Set<Key> getKeysForIndirectIds(String indirectType,
			Collection<String> indirectIds, Set<String> entityType) {
		// TODO Auto-generated method stub
		return null;
	}

}

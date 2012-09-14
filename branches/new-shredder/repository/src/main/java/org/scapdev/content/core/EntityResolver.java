/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2012 540951
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
package org.scapdev.content.core;

import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.IKeyedEntity;
import gov.nist.scap.content.shredder.parser.IEntityResolver;

public class EntityResolver implements IEntityResolver {

//	@Autowired
//	private MetadataStore ms;

	public EntityResolver() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public IKeyedEntity<?> resolveEntity(IKey key) {
		return null;
//		IKeyDefinition keyDef = ms.getOntology()
//				.getKeyById(cid.getKeyUri());
//
//		if (keyDef == null) {
//			throw new IllegalArgumentException("Could not find key: "
//					+ cid.getKeyUri());
//		}
//
//		KeyBuilder builder = new KeyBuilder(keyDef.getFields());
//		builder.setId(keyDef.getId());
//		if (keyDef.getFields().size() != cid.getKeyValues().size()) {
//			throw new IllegalArgumentException(
//					"Number of field values does not match key fields. Expecting "
//							+ keyDef.getFields().size() + " fields.");
//		}
//		
//		for (int i=0,size = keyDef.getFields().size(); i<size; i++) {
//			builder.addField(keyDef.getFields().get(i).getName(), cid.getKeyValues().get(i));
//		}
//		IKey key = builder.toKey();
//
//		Field[] f = new Field[key.getFieldNames().size()];
//		Iterator<String> iter = key.getFieldNames().iterator();
//		String fieldName;
//		for (int j = 0, size = key.getFieldNames().size(); j < size; j++) {
//			fieldName = iter.next();
//			f[j] = field(fieldName, key.getValue(fieldName));
//		}
//
//		EntityQuery query = selectEntitiesWith(allOf(key(key.getId(), f)));
//
//		
//		@SuppressWarnings("rawtypes")
//		Collection<? extends IKeyedEntity> retVal = ms.getEntities(query,
//				KeyedEntityProxy.class);
	}
}

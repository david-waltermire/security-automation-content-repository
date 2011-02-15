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

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import org.scapdev.content.model.jaxb.FieldRefType;
import org.scapdev.content.model.jaxb.KeyRefType;

class KeyRefInfoImpl implements KeyRefInfo {
	private final LocalRelationshipInfo parent;
	private final String id;
	private final List<FieldRefInfo> fields;
	private final KeyRefBindingInfo binding;
	private final KeyInfo referencedKey;

	KeyRefInfoImpl(KeyRefType keyRef, LocalRelationshipInfo parent, JAXBMetadataModel loader, InitializingJAXBClassVisitor init) {
		this.id = keyRef.getId();
		this.parent = parent;
		binding = init.getKeyRefBindingInfo(id);
		referencedKey = loader.getEntityByKeyId(keyRef.getIdRef()).getKeyInfo();

		List<FieldRefInfo> fields = new ArrayList<FieldRefInfo>(keyRef.getFieldRef().size());
		for (FieldRefType field : keyRef.getFieldRef()) {
			fields.add(new FieldRefInfoImpl(field, this, binding.getPropertyMap().get(field.getId()), loader));
		}
		this.fields = Collections.unmodifiableList(fields);
	}

	@Override
	public String getId() {
		return id;
	}

	KeyRefBindingInfo getBinding() {
		return binding;
	}

	@Override
	public SchemaInfo getSchemaInfo() {
		return parent.getSchemaInfo();
	}

	@Override
	public KeyInfo getKeyInfo() {
		return referencedKey;
	}

	private LinkedHashMap<String, String> getKeyValues(Object instance) throws ModelInstanceException {
		LinkedHashMap<String, String> fieldRefIdToValueMap = new LinkedHashMap<String, String>();
		for (FieldRefInfo fieldRefInfo : fields) {
			String value;
			try {
				value = fieldRefInfo.getValue(instance);
			} catch (IllegalArgumentException e) {
				throw new ModelInstanceException(e);
			} catch (IllegalAccessException e) {
				throw new ModelInstanceException(e);
			} catch (InvocationTargetException e) {
				throw new ModelInstanceException(e);
			}
			fieldRefIdToValueMap.put(fieldRefInfo.getReferencedFieldInfo().getId(), value);
		}
		return fieldRefIdToValueMap;
	}

	@Override
	public Key getKey(Object instance) throws ModelInstanceException {
		// TODO: Handle key collections
		return new Key(getKeyInfo().getId(), getKeyValues(instance));
	}
}

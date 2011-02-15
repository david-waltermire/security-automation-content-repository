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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.scapdev.content.model.jaxb.FieldType;
import org.scapdev.content.model.jaxb.KeyType;

class KeyInfoImpl implements KeyInfo {
	private final EntityInfo parent;
	private final String id;
	private final Map<String, FieldInfo> fields;
	private final KeyBindingInfo binding;

	KeyInfoImpl(KeyType key, EntityInfo parent, JAXBMetadataModel loader, InitializingJAXBClassVisitor init) {
		this.parent = parent;
		id = key.getId();
		binding = init.getKeyBindingInfo(id);

		Map<String, FieldInfo> fields = new LinkedHashMap<String, FieldInfo>();
		for (FieldType field : key.getField()) {
			FieldInfo info = new FieldInfoImpl(field, this, binding.getPropertyMap().get(field.getId()), loader);
			fields.put(info.getId(), info);
		}
		this.fields = Collections.unmodifiableMap(fields);
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public Collection<FieldInfo> getFieldInfos() {
		return fields.values();
	}

	@Override
	public EntityInfo getEntity() {
		return parent;
	}

	@Override
	public Key getKey(Object instance) throws KeyException {
		Key key = new Key(getId(), getFieldMap(instance));
		return key;
	}

	private LinkedHashMap<String, String> getFieldMap(Object instance) throws KeyException {
		LinkedHashMap<String, String> fieldIdToValueMap = new LinkedHashMap<String, String>();
		for (FieldInfo fieldInfo : getFieldInfos()) {
			String value;
			try {
				value = fieldInfo.getValue(instance);
			} catch (IllegalArgumentException e) {
				throw new KeyException(getId(),e);
			} catch (IllegalAccessException e) {
				throw new KeyException(getId(),e);
			} catch (InvocationTargetException e) {
				throw new KeyException(getId(),e);
			}
			fieldIdToValueMap.put(fieldInfo.getId(), value);
		}
		return fieldIdToValueMap;
	}

	@Override
	public SchemaInfo getSchemaInfo() {
		return parent.getSchemaInfo();
	}

	@Override
	public FieldInfo getFieldInfo(String id) {
		return fields.get(id);
	}

	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
			.append("id",id)
			.append("fields",fields)
			.toString();
	}
}

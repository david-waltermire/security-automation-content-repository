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
package org.scapdev.jaxb.reflection.model.jaxb;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;

import org.scapdev.jaxb.reflection.model.AbstractTypeInfo;
import org.scapdev.jaxb.reflection.model.DefaultTypeInfo;
import org.scapdev.jaxb.reflection.model.ExtendedModel;
import org.scapdev.jaxb.reflection.model.ModelFactory;
import org.scapdev.jaxb.reflection.model.SchemaModel;


public class DefaultModel implements ExtendedModel<DefaultTypeInfo> {
	private final JAXBModel<DefaultTypeInfo, DefaultPropertyInfo> internalModel;
	private Map<Class<?>, DefaultTypeInfo> classToTypeInfoMap;
	private final JAXBPropertyValueFactory propertyValueFactory;

	public DefaultModel(ClassLoader classLoader) {
		propertyValueFactory = new JAXBPropertyValueFactory(this);
		Factory factory = new Factory();
		internalModel = new JAXBModel<DefaultTypeInfo, DefaultPropertyInfo>(classLoader, factory);
		internalModel.processTypeProperties(factory);
	}

	public Collection<DefaultTypeInfo> getTypeInfos() {
		return classToTypeInfoMap.values();
	}
	@Override
	public DefaultTypeInfo getTypeInfo(Class<?> clazz) {
		return classToTypeInfoMap.get(clazz);
	}

	public JAXBContext getJAXBContext() {
		return internalModel.getJAXBContext();
	}

	private class Factory implements ModelFactory<DefaultTypeInfo, DefaultPropertyInfo> {
		private void registerPropertyInfo(DefaultPropertyInfo propertyInfo) {
		}

		@Override
		public DefaultPropertyInfo newPropertyInfo(Field field, AbstractTypeInfo<DefaultPropertyInfo> typeInfo) {
			DefaultPropertyInfo propertyInfo =  new DefaultPropertyInfo(field, typeInfo, propertyValueFactory);
			registerPropertyInfo(propertyInfo);
			return propertyInfo;
		}

		@Override
		public DefaultTypeInfo newTypeInfo(Class<?> clazz, SchemaModel schemaModel) {
			DefaultTypeInfo result;
			if (classToTypeInfoMap == null) {
				classToTypeInfoMap = new HashMap<Class<?>, DefaultTypeInfo>();
				result = null;
			} else {
				result = classToTypeInfoMap.get(clazz);
			}
			if (result == null) {
				result = new DefaultTypeInfo(clazz, schemaModel);
				classToTypeInfoMap.put(clazz, result);
			}
			return result;
		}
	}
}

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

import java.io.IOException;
import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.scapdev.jaxb.reflection.JAXBContextFactory;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.ModelFactory;
import org.scapdev.jaxb.reflection.model.MutableModel;
import org.scapdev.jaxb.reflection.model.MutableTypeInfo;
import org.scapdev.jaxb.reflection.model.PropertyInfo;

public class JAXBModel<X extends MutableTypeInfo<Y>, Y extends PropertyInfo> implements MutableModel<Y> {
	private static Logger log = Logger.getLogger(JAXBModel.class);

	private final JAXBContext context;
	/**
	 * a mapping of XML schema namespace keys to JAXBSchemaModel values
	 */
	private final Map<URI, InternalSchemaModel<X>> schemaModels = new TreeMap<URI, InternalSchemaModel<X>>();
	private final Map<Class<?>, X> dataTypes;

	protected InternalSchemaModel<X> newSchemaModel(ClassLoader contextClassLoader, String packageName, ModelFactory<X, Y> factory) {
		return new DefaultSchemaModel<X>(contextClassLoader, packageName, factory);
	}

	public JAXBModel(ClassLoader classLoader, ModelFactory<X, Y> factory) {
	
		try {
			context = JAXBContextFactory.getJAXBContext(classLoader);
		} catch (IOException e) {
			throw new JAXBModelException(e);
		} catch (JAXBException e) {
			throw new JAXBModelException(e);
		}

		log.info("Creating JAXBModel");
		for (String p : JAXBContextFactory.getPackagesForContext(context)) {
			log.info("Analyzing package: "+p);

			InternalSchemaModel<X> model = newSchemaModel(JAXBContextFactory.class.getClassLoader(), p, factory);
			schemaModels.put(model.getNamespace(), model);
		}
		this.dataTypes = new HashMap<Class<?>, X>();
		for (InternalSchemaModel<X> model : schemaModels.values()) {
			// Index data types
			dataTypes.putAll(model.getTypeInfos());
			log.info("Processed schema: "+model.getNamespace()+" package: "+model.getJaxbPackage());
		}
		processTypeHierarchies();
		validateModel();
	}

	/** {@inheritDoc} */
	public JAXBContext getJAXBContext() {
		return context;
	}

	/** {@inheritDoc} */
	public X getTypeInfo(Class<?> clazz) {
		return dataTypes.get(clazz);
	}

	public Collection<X> getTypeInfos() {
		return dataTypes.values();
	}

	private void processTypeHierarchies() {
		log.info("Processing parent types");
		for (X typeInfo : dataTypes.values()) {
			Class<?> clazz = typeInfo.getType();

			// Process parent
			Class<?> parentClazz = clazz.getSuperclass();
			if (parentClazz != null) {
				typeInfo.setParent(dataTypes.get(parentClazz));
			}
		}
	}

	public void processTypeProperties(ModelFactory<X, Y> factory) {
		log.info("Processing type properties");
		for (X typeInfo : dataTypes.values()) {
			typeInfo.generateProperties(factory);
		}
	}

	protected void validateModel() {
	}
}

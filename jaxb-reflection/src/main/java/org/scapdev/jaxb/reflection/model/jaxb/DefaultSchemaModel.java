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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlSchema;

import org.apache.log4j.Logger;
import org.scapdev.jaxb.reflection.JAXBContextFactory;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.ModelFactory;
import org.scapdev.jaxb.reflection.model.MutableTypeInfo;
import org.scapdev.jaxb.reflection.model.SchemaInfo;

public class DefaultSchemaModel<X extends MutableTypeInfo<?>> extends AbstractSchemaModel<X> {
	private static Logger log = Logger.getLogger(DefaultSchemaModel.class);

	@SuppressWarnings("unused")
	private final ClassLoader classLoader;
	private final String jaxbPackage;
	protected Map<Class<?>, X> types = new HashMap<Class<?>, X>();;

	public DefaultSchemaModel(ClassLoader classLoader, String jaxbPackage, ModelFactory<X, ?> factory) throws JAXBModelException {
		super(processSchema(jaxbPackage));
		this.classLoader = classLoader;
		this.jaxbPackage = jaxbPackage;
		processClasses(classLoader, jaxbPackage, factory);
	}

	/**
	 * @return the jaxbPackage
	 */
	public String getJaxbPackage() {
		return jaxbPackage;
	}

	public Map<Class<?>, X> getTypeInfos() {
		return types;
	}

	protected X getTypeInfo(Class<?> clazz) {
		return types.get(clazz);
	}

	protected void processClasses(ClassLoader classLoader, String jaxbPackage, ModelFactory<X, ?> factory)
			throws JAXBModelException {
		try {
			for (Class<?> clazz : loadClasses(classLoader, jaxbPackage)) {
				processClass(clazz, factory);
			}
		} catch (IOException e) {
			throw new JAXBModelException(e);
		} catch (ClassNotFoundException e) {
			throw new JAXBModelException(e);
		}
	}

	protected void processClass(Class<?> clazz, ModelFactory<X, ?> factory) {
		log.debug("Processing class: "+clazz);
		if (types.containsKey(clazz)) {
			throw new JAXBModelException("Duplicate class: "+clazz.getName());
		}

		X typeInfo = factory.newTypeInfo(clazz, this);
		types.put(clazz, typeInfo);
	}

	private static SchemaInfo processSchema(String jaxbPackage) {
		Package p = Package.getPackage(jaxbPackage);
		XmlSchema xmlSchema = p.getAnnotation(XmlSchema.class);
		return new SchemaInfo(URI.create(xmlSchema.namespace()));
	}
	
	private static Set<Class<?>> loadClasses(ClassLoader classLoader, String jaxbPackage)
			throws IOException, ClassNotFoundException {
		Set<String> classNames = loadJaxbProperties(classLoader, jaxbPackage);
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		for (String className : classNames) {
			String fullClassName = new StringBuilder(jaxbPackage).append('.').append(className).toString();
//			log.error(System.getProperty("java.class.path"));
			Class<?> clazz = classLoader.loadClass(fullClassName);
			classes.add(clazz);
		}
		return Collections.unmodifiableSet(classes);
	}

	private static Set<String> loadJaxbProperties(ClassLoader classLoader, String jaxbPackage)
			throws IOException {
				String jaxbIndex = packageToPath(jaxbPackage)+"/jaxb.index";
				InputStream is = classLoader.getResourceAsStream(jaxbIndex);
				if (is == null) {
					is = JAXBContextFactory.class.getResourceAsStream(jaxbIndex);
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			
				Set<String> classes = new LinkedHashSet<String>();
				String line;
				while ((line = reader.readLine()) != null) {
					classes.add(line);
				}
				reader.close();
				return Collections.unmodifiableSet(classes);
			
			}

	private static String packageToPath(String p) {
		return "/"+p.replace('.', '/');
	}
}

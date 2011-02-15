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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSchema;

import org.scapdev.jaxb.reflection.model.JAXBClass;
import org.scapdev.jaxb.reflection.model.JAXBClassImpl;
import org.scapdev.jaxb.reflection.model.JAXBModelException;
import org.scapdev.jaxb.reflection.model.JAXBPackage;
import org.scapdev.jaxb.reflection.model.visitor.JAXBClassVisitor;


public class JAXBPackageImpl implements JAXBPackage {
	private final Package jaxbPackage;
	private final JAXBModel jaxbModel;
	private final XmlSchema annotation;
	private final Map<Class<?>, JAXBClass> classMap;

	public JAXBPackageImpl(Package jaxbPackage, JAXBModel jaxbModel) {
		this.jaxbPackage = jaxbPackage;
		this.jaxbModel = jaxbModel;

		String name = jaxbPackage.getName();
		XmlSchema xmlSchema = jaxbPackage.getAnnotation(XmlSchema.class);
		if (xmlSchema == null) {
			throw new JAXBModelException("Invalid JAXB package: "+name);
		}
		this.annotation = xmlSchema;
		this.classMap = new HashMap<Class<?>, JAXBClass>();
	}
	  
	/* (non-Javadoc)
	 * @see org.scapdev.jaxb.reflection.model.visitor2.JAXBPackage#getNamespace()
	 */
	public String getNamespace() {
		return annotation.namespace();
	}

	/* (non-Javadoc)
	 * @see org.scapdev.jaxb.reflection.model.visitor2.JAXBPackage#getSchemaLocation()
	 */
	public String getSchemaLocation() {
		return annotation.location();
	}

	/* (non-Javadoc)
	 * @see org.scapdev.jaxb.reflection.model.visitor2.JAXBPackage#getJAXBModel()
	 */
	public JAXBModel getJAXBModel() {
		return jaxbModel;
	}

	/* (non-Javadoc)
	 * @see org.scapdev.jaxb.reflection.model.visitor2.JAXBPackage#getClass(java.lang.Class)
	 */
	public JAXBClass getClass(Class<?> clazz) {
		JAXBClass result = classMap.get(clazz);
		if (result == null) {
			result = new JAXBClassImpl(clazz, this);
			classMap.put(clazz, result);
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see org.scapdev.jaxb.reflection.model.visitor2.JAXBPackage#getXmlAccessType()
	 */
	public XmlAccessType getXmlAccessType() {
		XmlAccessorType annotation = jaxbPackage.getAnnotation(XmlAccessorType.class);
		XmlAccessType result;
		if (annotation == null) {
			result = XmlAccessType.PUBLIC_MEMBER;
		} else {
			result = annotation.value();
		}
		return result;
	}

	public void visit(JAXBClassVisitor visitor) throws ClassNotFoundException, IOException {
		initClasses();
		for (JAXBClass jaxbClass : classMap.values()) {
			visitor.visit(jaxbClass);
		}
	}

	private void initClasses() throws ClassNotFoundException, IOException {
		ClassLoader cl = jaxbModel.getClassLoader();
		String packageName = jaxbPackage.getName();
		Set<String> classNames = loadJaxbProperties(cl, packageName);
		Set<Class<?>> classes = new LinkedHashSet<Class<?>>();
		for (String className : classNames) {
			String fullClassName = new StringBuilder(packageName).append('.').append(className).toString();
//			log.error(System.getProperty("java.class.path"));
			Class<?> clazz = cl.loadClass(fullClassName);
			getClass(clazz);
			classes.add(clazz);
		}
	}

	private static Set<String> loadJaxbProperties(ClassLoader classLoader, String jaxbPackage)
			throws IOException {
		String jaxbIndex = packageToPath(jaxbPackage)+"/jaxb.index";
		InputStream is = classLoader.getResourceAsStream(jaxbIndex);
		if (is == null) {
			is = classLoader.getResourceAsStream(jaxbIndex.substring(1));
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

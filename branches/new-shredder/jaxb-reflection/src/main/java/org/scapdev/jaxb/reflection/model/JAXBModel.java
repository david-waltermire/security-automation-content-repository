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
package org.scapdev.jaxb.reflection.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.scapdev.jaxb.reflection.model.visitor.JAXBClassVisitor;

public class JAXBModel {
	private final ClassLoader classLoader;
	private Map<String, JAXBPackage> packageToSchemaMap;

	public JAXBModel(Collection<Package> jaxbPackages, ClassLoader classLoader) {
		this.classLoader = classLoader;
		packageToSchemaMap = new HashMap<String, JAXBPackage>();
		for (Package jaxbPackage : jaxbPackages) {
			packageToSchemaMap.put(jaxbPackage.getName(), new JAXBPackageImpl(jaxbPackage, this));
		}
	}

	public static JAXBModel newInstanceFromPackageNames(Collection<String> packageNames, ClassLoader classLoader) {
		List<Package> packages = new ArrayList<Package>(packageNames.size());
		for (String p : packageNames) {
			packages.add(Package.getPackage(p));
		}
		return new JAXBModel(packages, classLoader);
	}

	/**
	 * 
	 * @param clazz
	 * @return
	 */
	public JAXBClass getClass(Class<?> clazz) {
		String name = clazz.getPackage().getName();
		JAXBPackage jaxbPackage = packageToSchemaMap.get(name);
		if (jaxbPackage == null) {
			return null;
		}
		return jaxbPackage.getClass(clazz);
	}

	public void visit(JAXBClassVisitor visitor) throws ClassNotFoundException, IOException {
		for (JAXBPackage jaxbPackage : packageToSchemaMap.values()) {
			jaxbPackage.visit(visitor);
		}
	}

	public ClassLoader getClassLoader() {
		return classLoader;
	}
}

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
package org.scapdev.jaxb.reflection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.apache.log4j.Logger;
import org.scapdev.jaxb.reflection.model.JAXBModelException;

/**
 * This factory class produces a JAXBContext and manages Java packages related
 * to bound classes within the /META-INF/jaxb-manifest.  The contents of these
 * files are one package name per line.
 * 
 * @author David Waltermire
 *
 */
public class JAXBContextFactory {
	private static Logger log = Logger.getLogger(JAXBContextFactory.class);
	private static final ConcurrentMap<JAXBContext, ContextInfo> contextMap = new ConcurrentHashMap<JAXBContext, ContextInfo>();

	public static JAXBContext getJAXBContext(ClassLoader classLoader) throws IOException, JAXBException {
		ContextInfo info = new ContextInfo(classLoader);
		contextMap.put(info.getContext(), info);
		return info.getContext();
	}

	public static Set<String> getPackagesForContext(JAXBContext context) {
		ContextInfo info = contextMap.get(context);
		if (info == null) {
			throw new JAXBModelException("the JAXBContext was not initialized by this factory");
		}
		return info.getPackages();
	}

	// TODO: this method will only find the manifest in a single jar associated
	// with the class loader.  It might be better to allow multiple classloaders
	// to be provided allowing multiple jars to be used.
	private static Set<String> getContextPackages(ClassLoader classLoader) throws IOException {
		log.info("Retrieving packages from jaxb-manifest");
		InputStream is = classLoader.getResourceAsStream("/META-INF/jaxb-manifest");
		if (is == null) {
			is = JAXBContextFactory.class.getResourceAsStream("/META-INF/jaxb-manifest");
		}
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));

		Set<String> contextPackages = new LinkedHashSet<String>();
		String line;
		while ((line = reader.readLine()) != null) {
			log.debug("Package found: "+line);
			contextPackages.add(line);
		}
		reader.close();
		return Collections.unmodifiableSet(contextPackages);
	}

	private static String buildContextPath(Set<String> contextPackages) {
		StringBuilder contextPath = new StringBuilder();
		for (String p : contextPackages) {
			if (contextPath.length() != 0) {
				contextPath.append(':');
			}
			contextPath.append(p);
		}
		return contextPath.toString();
	}

	static class ContextInfo {
		private final ClassLoader classLoader;
		private final Set<String> packages;
		private final JAXBContext context;

		public ContextInfo(ClassLoader classLoader) throws JAXBException, IOException {
			this.classLoader = classLoader;
			this.packages = getContextPackages(classLoader);

			log.info("Initializing JAXBContext");
			this.context = JAXBContext.newInstance(buildContextPath(packages), classLoader);
			log.info("JAXBContext created");
		}

		/**
		 * @return the classLoader
		 */
		public ClassLoader getClassLoader() {
			return classLoader;
		}

		/**
		 * @return the packages
		 */
		public Set<String> getPackages() {
			return packages;
		}

		/**
		 * @return the context
		 */
		public JAXBContext getContext() {
			return context;
		}

		
	}
}

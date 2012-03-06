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
package org.scapdev.content.util;

import java.util.ServiceLoader;

import org.apache.log4j.Logger;

public class ServiceLoaderUtil {
	private static final Logger log = Logger.getLogger(ServiceLoaderUtil.class);

	/**
	 * Loads a service instance using the Java SPI. The service class instance
	 * retrieved will be the class described by the value of the provided
	 * property if it is available; or the default instance if no matching class
	 * was found.
	 * 
	 * @param <SERVICE_CLASS> the base type of the service to load
	 * @param serviceClass the class representing the service to load
	 * @param property the name of a system property that describes the class
	 * 		name of the service instance to load
	 * @param defaultInstance the class of the service instance to return if
	 * 		no instance matches the <code>property</code> argument 
	 * @return a service instance
	 * @throws UnsupportedOperationException if an instance was not found that
	 * 		matches the provided property and the defaultInstance
	 * @see java.util.ServiceLoader#load(Class)
	 */
	public static <SERVICE_CLASS> SERVICE_CLASS load(Class<SERVICE_CLASS> serviceClass, String property, Class<? extends SERVICE_CLASS> defaultInstance) {
		return load(serviceClass, property, defaultInstance, null);
	}

	/**
	 * Loads a service instance using the Java SPI. The service class instance
	 * retrieved will be the class described by the value of the provided
	 * property if it is available; or the default instance if no matching class
	 * was found.
	 * 
	 * @param <SERVICE_CLASS> the base type of the service to load
	 * @param serviceClass the class representing the service to load
	 * @param property the name of a system property that describes the class
	 * 		name of the service instance to load
	 * @param defaultInstance the class of the service instance to return if
	 * 		no instance matches the <code>property</code> argument 
	 * @param classLoader the class loader to use or <code>null</code>
	 * 		indicating that the system class loader should be used.
	 * @return a service instance
	 * @throws UnsupportedOperationException if an instance was not found that
	 * 		matches the provided property and the defaultInstance
	 * @see java.util.ServiceLoader#load(Class, ClassLoader)
	 */
	public static <SERVICE_CLASS> SERVICE_CLASS load(Class<SERVICE_CLASS> serviceClass, String property, Class<? extends SERVICE_CLASS> defaultInstance, ClassLoader classLoader) {
		ServiceLoader<? extends SERVICE_CLASS> loader = ServiceLoader.load(serviceClass, classLoader);
		String propertyValue = System.getProperty(property);

		SERVICE_CLASS result = null;
		SERVICE_CLASS defaultFactory = null;
		for (SERVICE_CLASS service : loader) {
			if (service.getClass().equals(defaultInstance)) {
				defaultFactory = service;
			}
			
			if (service.getClass().getName().equals(propertyValue)) {
				result = service;
				break;
			}
		}
		if (result == null) {
			if (defaultFactory == null) {
				UnsupportedOperationException e = new UnsupportedOperationException("Default service is not registered: "+defaultInstance);
				log.error("Unable to load service", e);
				throw e;
			}
			result = defaultFactory;
		}
		log.info("Using service instance: "+result.getClass());
		return result;
	}
}

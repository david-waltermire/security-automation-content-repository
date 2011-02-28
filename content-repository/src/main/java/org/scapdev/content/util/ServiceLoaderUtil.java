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

	public static <FACTORY> FACTORY load(Class<FACTORY> serviceClass, String property, Class<? extends FACTORY> defaultInstance) {
		return load(serviceClass, property, defaultInstance, null);
	}

	public static <FACTORY> FACTORY load(Class<FACTORY> serviceClass, String property, Class<? extends FACTORY> defaultInstance, ClassLoader classLoader) {
		ServiceLoader<? extends FACTORY> loader = ServiceLoader.load(serviceClass, classLoader);
		String propertyValue = System.getProperty(property);

		FACTORY result = null;
		FACTORY defaultFactory = null;
		for (FACTORY service : loader) {
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

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
package org.scapdev.util;

import java.lang.annotation.Annotation;

/**
 * Utility methods that support identifying annotations on a class or a super class recursively.
 * 
 * @author David Waltermire
 *
 */
public class AnnotationUtil {

	/**
	 * Retrieves an annotation from a class or one of its super classes.
	 * @param <A> the annotation type to retrieve
	 * @param clazz the class to search for an annotation
	 * @param annotationClass the annotation type to search for
	 * @return the annotation if it exists on the class or a super class or
	 * 		<code>null</code> if the annotation is not present
	 */
	public static <A extends Annotation> A getAnnotationRecursive(Class<?> clazz, Class<A> annotationClass) {
		do {
			A result = clazz.getAnnotation(annotationClass);
			if (result != null) {
				return result;
			}
		} while ((clazz = clazz.getSuperclass()) != null);
		return null;
	}

	/**
	 * Identifies if an annotation is present on a class or one of its super classes.
	 * @param <A> the annotation type to identify
	 * @param clazz the class to search for an annotation
	 * @param annotationClass the annotation type to search for
	 * @return <code>true</code> if the annotation exists on the class or a super class or
	 * 		<code>false</code> if the annotation is not present
	 * @return
	 */
	public static <A extends Annotation> boolean isAnnotationPresentRecursive(Class<?> clazz, Class<A> annotationClass) {
		do {
			if (clazz.isAnnotationPresent(annotationClass)) {
				return true;
			}
		} while ((clazz = clazz.getSuperclass()) != null);
		return false;
	}
}

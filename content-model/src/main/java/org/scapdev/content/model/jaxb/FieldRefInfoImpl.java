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
package org.scapdev.content.model.jaxb;

import java.util.List;

import org.scapdev.content.model.FieldInfo;
import org.scapdev.content.model.FieldRefInfo;
import org.scapdev.content.model.KeyInfo;
import org.scapdev.content.model.KeyRefInfo;
import org.scapdev.jaxb.reflection.model.jaxb.DefaultPropertyInfo;

class FieldRefInfoImpl extends AbstractFieldInfo<FieldRefType, KeyRefInfo> implements FieldRefInfo {
	private final FieldInfo referencedFieldInfo;
	
	FieldRefInfoImpl(FieldRefType field, KeyRefInfo parent, List<DefaultPropertyInfo> propertyPath, JAXBMetadataModel loader) {
		super(field, parent, propertyPath, loader);
		KeyInfo keyInfo = parent.getKeyInfo();
		String fieldRef = field.idRef;
		referencedFieldInfo = keyInfo.getFieldInfo(fieldRef);
	}

	@Override
	public KeyRefInfo getKeyRefInfo() {
		return getParent();
	}

	@Override
	public FieldInfo getReferencedFieldInfo() {
		return referencedFieldInfo;
	}
}

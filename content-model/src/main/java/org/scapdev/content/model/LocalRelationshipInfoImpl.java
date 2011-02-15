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
package org.scapdev.content.model;

import org.scapdev.content.model.jaxb.LocalRelationshipType;

class LocalRelationshipInfoImpl extends AbstractRelationshipInfo implements LocalRelationshipInfo {

	private final KeyRefInfoImpl keyRefInfo;

	public LocalRelationshipInfoImpl(LocalRelationshipType type, SchemaInfo schemaInfo, JAXBMetadataModel loader, InitializingJAXBClassVisitor init) {
		super(type, schemaInfo);
		keyRefInfo = new KeyRefInfoImpl(type.getKeyRef(), this, loader, init);
	}

	@Override
	public KeyRefInfoImpl getKeyRefInfo() {
		return keyRefInfo;
	}

	@Override
	public KeyInfo getKeyInfo() {
		return keyRefInfo.getKeyInfo();
	}

	@Override
	public Key getKey(Object instance) throws ModelInstanceException {
		return keyRefInfo.getKey(instance);
	}

	@Override
	public LocalRelationship newRelationship(Object instance, Entity owningEntity) {
		return new LocalRelationshipImpl<Object>(this, owningEntity, getKey(instance));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof LocalRelationshipInfoImpl))
			return false;
		LocalRelationshipInfoImpl other = (LocalRelationshipInfoImpl) obj;
		if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int result = 13;
		result = 37 * result +  getId().hashCode();
		return result;
	}
}

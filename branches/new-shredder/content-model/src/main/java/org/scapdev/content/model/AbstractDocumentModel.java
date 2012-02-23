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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.scapdev.content.model.jaxb.DocumentModelType;
import org.scapdev.content.model.jaxb.EntityContainerType;
import org.scapdev.content.model.jaxb.EntityContainersType;
import org.scapdev.content.model.jaxb.EntityIdRefType;

public abstract class AbstractDocumentModel<MODEL_TYPE extends DocumentModelType> implements DocumentModel {

	protected final MODEL_TYPE type;
	private Set<EntityInfo> entityIdRefs = null;

	public AbstractDocumentModel(MODEL_TYPE type) {
		this.type = type;
	}

	protected MODEL_TYPE getDocumentModelType() {
		return type;
	}

	@Override
	public Set<EntityInfo> getSupportedEntityInfos(MetadataModel model) {
		Set<EntityInfo> result;
		if (entityIdRefs == null) {
			EntityContainersType entityContainers = type.getEntityContainers();
			if (entityContainers != null) {
				result = new HashSet<EntityInfo>();
				for (EntityContainerType entityContainerType : type.getEntityContainers().getEntityContainer()) {
					for (EntityIdRefType idRefType : entityContainerType.getEntityRef()) {
						String idRef = idRefType.getIdRef();
						EntityInfo entityInfo = model.getEntityInfoById(idRef);
						result.add(entityInfo);
					}
				}
				entityIdRefs = Collections.unmodifiableSet(result);
			} else {
				entityIdRefs = Collections.emptySet();
			}
		}
		return entityIdRefs;
	}

}
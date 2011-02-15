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

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.scapdev.content.model.jaxb.SchemaComponentType;
import org.scapdev.jaxb.reflection.model.JAXBProperty;
import org.scapdev.jaxb.reflection.model.instance.PropertyPathEvaluator;

abstract class AbstractFieldInfo<JAXB_TYPE extends SchemaComponentType, PARENT extends Component> implements IndexFieldInfo {

	private final PARENT parent;
	private final String id;
	private final String schemaNode;
	private final List<JAXBProperty> propertyPath;
	
	AbstractFieldInfo(JAXB_TYPE field, PARENT parent, List<JAXBProperty> propertyPath, JAXBMetadataModel loader) {
		this.parent = parent;
		id = field.getId();
		schemaNode = field.getSchemaNode().getNode();
		this.propertyPath = propertyPath;
//		binding = loader.getFieldBindingInfo(id);
	}

	@Override
	public String getId() {
		return id;
	}

	public PARENT getParent() {
		return parent;
	}

	@Override
	public String getSchemaNode() {
		return schemaNode;
	}

	@Override
	public SchemaInfo getSchemaInfo() {
		return getParent().getSchemaInfo();
	}

	public List<JAXBProperty> getPropertyPath() {
		return propertyPath;
	}

	@Override
	public String getValue(Object instance) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		return PropertyPathEvaluator.evaluate(instance, getPropertyPath());
	}


	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
			.appendSuper(super.toString())
			.append("id",id)
			.append("propertyPath",propertyPath)
			.toString();
	}
}

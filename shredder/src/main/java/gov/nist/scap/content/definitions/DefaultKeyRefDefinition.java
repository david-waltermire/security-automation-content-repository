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
package gov.nist.scap.content.definitions;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.KeyBuilder;
import gov.nist.scap.content.model.KeyException;

import java.util.List;
import java.util.Map;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;

public class DefaultKeyRefDefinition extends AbstractKeyedDefinition implements IKeyRefDefinition {
	private final IKeyDefinition keyDefinition;

	public DefaultKeyRefDefinition(ISchema schema, String id,
			List<? extends IKeyedField> fields, IKeyDefinition keyDefinition) throws XmlException {
		super(schema, id, fields);
		this.keyDefinition = keyDefinition;
	}

	public IKeyDefinition getKeyDefinition() {
		return keyDefinition;
	}

	public IKey getKey(IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ContentException {
		Map<String, String> referenceFieldIdToValueMap = getFieldValues(parentContext, cursor);

//		List<? extends IKeyedField> fields = getFields();
//
//		Map<String, String> keyFieldIdToValueMap = new HashMap<String, String>();
//		for (IKeyedField field : fields) {
//			String referenceFieldName = field.getId();
//			String keyFieldName = field.getKeyFieldDefinition().getId();
//			keyFieldIdToValueMap.put(keyFieldName, referenceFieldIdToValueMap.get(referenceFieldName));
//		}
		KeyBuilder builder = new KeyBuilder(getKeyDefinition().getFields());
		builder.setId(keyDefinition.getId());
//		builder.addFields(keyFieldIdToValueMap);
		builder.addFields(referenceFieldIdToValueMap);
		return builder.toKey();
	}

//	private LinkedHashMap<String, String> getKeyValues(Object instance) throws ModelInstanceException {
//		LinkedHashMap<String, String> fieldRefIdToValueMap = new LinkedHashMap<String, String>();
//		for (FieldRefInfo fieldRefInfo : fields) {
//			String value = fieldRefInfo.getValue(instance);
//			fieldRefIdToValueMap.put(fieldRefInfo.getReferencedFieldInfo().getId(), value);
//		}
//		return fieldRefIdToValueMap;
//	}
//
//	/** {@inheritDoc} */
//	public Key newKey(Object instance) throws ModelInstanceException {
//		// TODO: Handle key collections
//		return new Key(getKeyInfo().getId(), getKeyValues(instance));
//	}
}

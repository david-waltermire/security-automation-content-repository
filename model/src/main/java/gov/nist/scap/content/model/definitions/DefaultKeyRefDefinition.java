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
package gov.nist.scap.content.model.definitions;

import java.util.List;

public class DefaultKeyRefDefinition implements IKeyRefDefinition {
	private final KeyedSupport keyedSupport;
	private final IKeyDefinition keyDefinition;

	public DefaultKeyRefDefinition(List<? extends IKeyedField> fields, IKeyDefinition keyDefinition) {
		this.keyedSupport = new KeyedSupport(fields);
		this.keyDefinition = keyDefinition;
	}

	public List<? extends IKeyedField> getFields() {
		return keyedSupport.getFields();
	}

	public IKeyDefinition getKeyDefinition() {
		return keyDefinition;
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

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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.scapdev.content.model.jaxb.IdentifierMappingType;
import org.scapdev.content.model.jaxb.KeyFieldMappingType;
import org.scapdev.content.model.jaxb.PatternOperationType;

class EntityIdentifierMappingImpl implements EntityIdentifierMapping {
	private final EntityInfo entity;
	private final List<PatternOperation> operations;

	public EntityIdentifierMappingImpl(IdentifierMappingType type, EntityInfo entity) {
		this.entity = entity;

		List<PatternOperationType> opTypes = type.getPatternOperation();
		operations = new ArrayList<PatternOperation>(opTypes.size());
		for (PatternOperationType opType : opTypes) {
			PatternOperation op = new PatternOperation(opType);
			operations.add(op);
		}
	}

	@Override
	public Key getKeyForIdentifier(String identifier) {
		Key result = null;
		for (PatternOperation op : operations) {
			result = op.getKeyForIdentifier(identifier);
			if (result != null) break;
		}
		return result;
	}

	private class PatternOperation {
		private final Pattern pattern;
		private final LinkedHashMap<String, String> keyFieldIdToValueMap;

		public PatternOperation(PatternOperationType opType) {
			String p = opType.getPattern();
			pattern = Pattern.compile(p);
			KeyInfo keyInfo = entity.getKeyInfo();
			keyFieldIdToValueMap = new LinkedHashMap<String, String>();
			for (KeyFieldMappingType fieldType : opType.getFieldValue()) {
				String fieldId = fieldType.getIdRef();
				FieldInfo fieldInfo = keyInfo.getFieldInfo(fieldId);
				String value = fieldType.getValue();

				keyFieldIdToValueMap.put(fieldInfo.getId(), value);
			}
		}

		public Key getKeyForIdentifier(String identifier) {
			Matcher matcher = pattern.matcher(identifier);

			Key result = null;
			if (matcher.matches()) {
				result = buildKey(matcher);
			}
			return result;
		}

		private Key buildKey(Matcher matcher) {
			LinkedHashMap<String, String> idToValueMap = new LinkedHashMap<String, String>();
			for (Map.Entry<String, String> entry : keyFieldIdToValueMap.entrySet()) {
				String fieldId = entry.getKey();
				String value = entry.getValue();
				value = matcher.replaceAll(value);
				idToValueMap.put(fieldId, value);
			}
			return new Key(entity.getKeyInfo().getId(), idToValueMap);
		}
		
	}
}

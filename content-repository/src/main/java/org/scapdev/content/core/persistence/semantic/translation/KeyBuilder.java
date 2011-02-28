/*******************************************************************************
 * The MIT License
 * 
 * Copyright (c) 2011 Paul Cichonski
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
package org.scapdev.content.core.persistence.semantic.translation;

import java.util.LinkedHashMap;

import org.scapdev.content.model.Key;

/**
 * A class to handle building a Key instance.
 *
 */
class KeyBuilder {
	private String id;
	private LinkedHashMap<String, String> idToValueMap = new LinkedHashMap<String, String>();
	
	KeyBuilder() {
		// TODO Auto-generated constructor stub
	}
	
	void setId(String id) {
		this.id = id;
	}
	
	void addKeyField(String fieldId, String fieldValue){
		idToValueMap.put(fieldId, fieldValue);
	}
	
	/**
	 * Builds a key out of all data added to builder.
	 * @return
	 */
	Key build(){
		if (id == null || id.isEmpty() || idToValueMap.isEmpty()){
			throw new IncompleteBuildStateException("Not all values are populated");
		}
		Key key = new Key(id, idToValueMap);
		return key;
	}
}

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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

import org.scapdev.content.model.jaxb.ExternalIdentifierType;

class ExternalIdentifierInfoImpl implements ExternalIdentifierInfo {
	private final String id;
	private final Collection<Pattern> patterns;

	public ExternalIdentifierInfoImpl(ExternalIdentifierType externalIdentifierType) {
		this.id = externalIdentifierType.getId();
		List<String> patternStrings = externalIdentifierType.getPattern();
		if (patternStrings != null) {
			Collection<Pattern> compiledPatterns = new ArrayList<Pattern>(patternStrings.size());
			for (String pattern : patternStrings) {
				compiledPatterns.add(Pattern.compile(pattern));
			}
			patterns = Collections.unmodifiableCollection(compiledPatterns);
		} else {
			patterns = Collections.emptyList();
		}
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public Collection<Pattern> getPattern() {
		return patterns;
	}

}

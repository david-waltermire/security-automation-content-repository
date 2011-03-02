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
package org.content.repository.war.rest.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlType
@XmlRootElement(name = "simpleResponse")
@XmlAccessorType(XmlAccessType.FIELD)
public class SimpleXMLResponse {
	
	
	// this will actually be used when the
	// response is serialized by jaxb
	@SuppressWarnings("unused")
	private ResultType result = ResultType.OK;

	// same as above, will actually be used
	// when response is serialized by jaxb
	@SuppressWarnings("unused")
	private String cause;
	
	// required by jaxb
	public SimpleXMLResponse()
	{	
	}
	
	private String buildStringFromTrace(Throwable cause)
	{
		StringBuilder sb = new StringBuilder();
		String eol = System.getProperty("line.separator");
		sb.append(cause.getMessage() + ":" + eol);
		
		StackTraceElement[] stacktrace = cause.getStackTrace();

		for(int x = 0; x < stacktrace.length; x++)
		{
			StackTraceElement element = stacktrace[x];
			sb.append("\t" + element.toString() + eol);
		}
		
		Throwable inner = cause.getCause();
		
		if(inner != null)
		{
			while(inner != null)
			{
				sb.append("Caused by" + eol);
				sb.append(buildStringFromTrace(inner));
				inner = inner.getCause();
			}
		}
		return sb.toString();
	}
	
	public SimpleXMLResponse(Throwable cause)
	{
		
		this.cause = buildStringFromTrace(cause);
		result = ResultType.ERROR;
	}
	 
	public void setCause(Throwable cause)
	{
		this.cause = buildStringFromTrace(cause);
		result = ResultType.ERROR;
	}
	
	private static enum ResultType
	{
		OK,
		ERROR
	}
}

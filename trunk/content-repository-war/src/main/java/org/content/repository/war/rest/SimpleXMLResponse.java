package org.content.repository.war.rest;

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

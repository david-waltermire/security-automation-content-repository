package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.IContainer;

import java.util.List;

import org.apache.xmlbeans.XmlCursor;

public interface IValueRetriever {
	String getValue(IContainer<?> entity, XmlCursor cursor) throws ProcessingException;
	List<String> getValues(IContainer<?> entity, XmlCursor cursor) throws ProcessingException;
}

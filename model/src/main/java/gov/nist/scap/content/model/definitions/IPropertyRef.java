package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.IContainer;

import java.util.List;

import org.apache.xmlbeans.XmlCursor;

public interface IPropertyRef {
	IPropertyDefinition getPropertyDefinition();
	List<String> getValues(IContainer<?> parentContext, XmlCursor cursor) throws ProcessingException;
}

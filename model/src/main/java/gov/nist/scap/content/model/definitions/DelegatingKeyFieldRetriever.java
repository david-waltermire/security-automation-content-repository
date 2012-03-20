package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;

import java.util.Collections;
import java.util.List;

import org.apache.xmlbeans.XmlCursor;

public class DelegatingKeyFieldRetriever implements IValueRetriever {
	private final String keyId;
	private final String fieldId;

	public DelegatingKeyFieldRetriever(String delegateKeyId, String delegateFieldId) {
		this.keyId = delegateKeyId;
		this.fieldId = delegateFieldId;
	}

	public String getValue(IContainer<?> parentContext, XmlCursor cursor)
			throws ProcessingException {
		IKey key = parentContext.getKey(keyId);
		if (key == null) {
			throw new ProcessingException("unable to find key '" + keyId + "' in parent context");
		}
		String retval = key.getValue(fieldId);
		if (retval == null) {
			throw new NullPointerException("result is null");
		}
		return retval;
	}

	public List<String> getValues(IContainer<?> entity, XmlCursor cursor)
			throws ProcessingException {
		return Collections.singletonList(getValue(entity, cursor));
	}
}

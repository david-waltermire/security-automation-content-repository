package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.KeyException;

import org.apache.xmlbeans.XmlCursor;

public class DelegatingKeyedField extends AbstractKeyedField {
	private final String keyId;
	private final String fieldId;

	public DelegatingKeyedField(String name, String delegateKeyId, String delegateFieldId) {
		super(name);
		this.keyId = delegateKeyId;
		this.fieldId = delegateFieldId;
	}

	@Override
	protected String retrieveValue(IContainer<?> parentContext, XmlCursor cursor)
			throws KeyException {
		IKey key = parentContext.getKey(keyId);
		if (key == null) {
			throw new KeyException("unable to find key '" + keyId + "' in parent context");
		}
		return key.getValue(fieldId);
	}
}

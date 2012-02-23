package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.IEntity;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.KeyException;

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
	protected String retrieveValue(IEntity<?> parentContext, XmlCursor cursor)
			throws KeyException {
		IKey key = parentContext.getKey(keyId);
		if (key == null) {
			throw new KeyException("unable to find key '" + keyId + "' in parent context");
		}
		return key.getValue(fieldId);
	}
}

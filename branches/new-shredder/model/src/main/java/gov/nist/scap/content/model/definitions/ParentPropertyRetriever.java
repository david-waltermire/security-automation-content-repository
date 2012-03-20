package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.IContainer;

import java.util.List;

import org.apache.xmlbeans.XmlCursor;

public class ParentPropertyRetriever implements IValueRetriever {
	private final String parentPropertyRef;

	public ParentPropertyRetriever(String parentPropertyRef) {
		this.parentPropertyRef = parentPropertyRef;
	}

	public String getValue(IContainer<?> parent, XmlCursor cursor) {
		List<String> result = getValuesInternal(parent, cursor);
		if (result.size() > 1) {
			throw new IllegalStateException("retrieved values are not singleton");
		}
		String retval = null;
		if (!result.isEmpty()) {
			retval = result.get(0);
		}
		return retval;
	}

	public List<String> getValues(IContainer<?> parent, XmlCursor cursor) {
		return getValuesInternal(parent, cursor);
	}

	protected List<String> getValuesInternal(IContainer<?> parent, XmlCursor cursor) {
		return parent.getPropertyById(parentPropertyRef);
	}
}

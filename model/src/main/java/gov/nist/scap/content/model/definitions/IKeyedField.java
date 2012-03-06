package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.KeyException;

import org.apache.xmlbeans.XmlCursor;

public interface IKeyedField {
	String getName();

	/**
	 * Must call {@link org.apache.xmlbeans.XmlCursor#push()} before and
	 * {@link org.apache.xmlbeans.XmlCursor#push()} after any call the modifies
	 * the cursor position.
	 * @param parentContext
	 * @param cursor
	 * @return
	 * @throws KeyException
	 * @throws ContentException 
	 */
	String getValue(IContainer<?> parentContext, XmlCursor cursor) throws ContentException;
}

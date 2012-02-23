package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IEntity;
import gov.nist.scap.content.shredder.model.KeyException;

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
	String getValue(IEntity<?> parentContext, XmlCursor cursor) throws ContentException;
}

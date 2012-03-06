package gov.nist.scap.content.model.definitions;

import gov.nist.scap.content.model.ContentException;
import gov.nist.scap.content.model.IContainer;
import gov.nist.scap.content.model.IKey;
import gov.nist.scap.content.model.KeyException;

import java.util.List;

import org.apache.xmlbeans.XmlCursor;

public interface IKeyDefinition extends IDefinition {
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
	IKey getKey(IContainer<?> parentContext, XmlCursor cursor) throws KeyException, ContentException;
	List<? extends IKeyedField> getFields();
}

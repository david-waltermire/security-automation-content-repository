package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IContainer;
import gov.nist.scap.content.shredder.model.IKey;
import gov.nist.scap.content.shredder.model.KeyException;

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

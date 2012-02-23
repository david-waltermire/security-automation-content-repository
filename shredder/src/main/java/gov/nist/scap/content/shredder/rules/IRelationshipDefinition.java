package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.parser.ContentHandler;

import org.apache.xmlbeans.XmlCursor;

public interface IRelationshipDefinition extends IDefinition {
	/**
	 * Must call {@link org.apache.xmlbeans.XmlCursor#push()} before and
	 * {@link org.apache.xmlbeans.XmlCursor#push()} after any call the modifies
	 * the cursor position.
	 * @param cursor
	 * @param handler
	 * @param containingEntity
	 * @throws ProcessingException
	 * @throws ContentException
	 */
	void processCursor(XmlCursor cursor, ContentHandler handler, IMutableEntity<?> containingEntity) throws ProcessingException, ContentException;
}

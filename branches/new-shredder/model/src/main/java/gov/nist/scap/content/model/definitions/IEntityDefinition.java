package gov.nist.scap.content.model.definitions;

import java.util.Collection;

public interface IEntityDefinition extends IDefinition {
	void addRelationship(IRelationshipDefinition relationship);
//	/**
//	 * Must call {@link org.apache.xmlbeans.XmlCursor#push()} before and
//	 * {@link org.apache.xmlbeans.XmlCursor#push()} after any call the modifies
//	 * the cursor position.
//	 * @param cursor
//	 * @param boundary
//	 * @param handler
//	 * @param parent
//	 * @throws ProcessingException
//	 * @throws ContentException
//	 */
//	IMutableEntity<?> processCursor(XmlCursor cursor, ContentHandler handler, IMutableEntity<?> parent) throws ProcessingException, ContentException;

	Collection<? extends IRelationshipDefinition> getRelationshipDefinitions();
	IVersionDefinition getVersionDefinition();
	<T> T accept(IEntityDefinitionVisitor<T> visitor) throws ProcessingException;
}

package gov.nist.scap.content.shredder.rules;

import gov.nist.scap.content.shredder.model.ContentException;
import gov.nist.scap.content.shredder.model.IMutableEntity;
import gov.nist.scap.content.shredder.parser.ContentHandler;

import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlRuntimeException;

public abstract class AbstractRelationshipDefinition extends AbstractDefinition implements IRelationshipDefinition {
	/**
	 * The xpath used to navigate to the XML element associated with this
	 * relationship. This may be <code>null</code> if the relationship is at the
	 * same location as the containing content node
	 */
	private final String xpath;

	public AbstractRelationshipDefinition(ISchema schema, String id, String xpath) throws XmlException {
		super(schema, id);

		if (xpath != null && xpath.isEmpty()) {
			throw new IllegalArgumentException("xpath must not be empty");
		}

		this.xpath = (xpath == null ? null : XmlBeans.compilePath(xpath));
	}

	protected abstract void handleRelationship(XmlCursor cursor,
			ContentHandler handler, IMutableEntity<?> containingEntity) throws ProcessingException, ContentException;

	public final void processCursor(XmlCursor cursor, ContentHandler handler, IMutableEntity<?> containingEntity) throws ProcessingException, ContentException {
		if (xpath != null) {
			cursor.push();
			try {
				// select the children of this boundary
				cursor.selectPath(xpath);
				int count = cursor.getSelectionCount();
				// iterate over the selected nodes
				for (int i = 0; i < count; i++) {
					if (!cursor.toSelection(i)) {
						throw new RuntimeException("unable to navigate to next cursor position");
					}
	
					XmlCursor newCursor = cursor.newCursor();
					handleRelationship(newCursor, handler, containingEntity);
				}
			} catch (XmlRuntimeException e) {
				throw new ProcessingException("Unable to select xpath for boundary: "+getId(), e);
			} finally {
				cursor.pop();
			}
		} else {
			handleRelationship(cursor, handler, containingEntity);
		}
	}
}

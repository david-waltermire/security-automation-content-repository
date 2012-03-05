package gov.nist.scap.content.shredder.model;

import org.apache.xmlbeans.XmlCursor;

public interface IContentHandle {
	XmlCursor getCursor();
	XmlCursor.XmlBookmark getBookmark();
}

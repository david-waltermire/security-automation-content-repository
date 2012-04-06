package gov.nist.scap.content.exist;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.exist.xmldb.EXistResource;
import org.exist.xmldb.LocalXMLResource;
import org.exist.xmldb.RemoteXMLResource;
import org.scapdev.content.core.persistence.hybrid.ContentRetriever;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 * An eXist-db implementation of content retriever
 * @author Adam Halbardier
 *
 */
public class ExistDBContentRetriever implements ContentRetriever {

    private Collection col;
    private String contentId;
    private XmlObject xmlObj;

    /**
     * default constructor
     * @param col the collection to retrieve content from
     * @param contentId the id of the content to retrieve
     */
    public ExistDBContentRetriever(Collection col, String contentId) {
        this.col = col;
        this.contentId = contentId;
    }

    @Override
    public XmlCursor getCursor() {
        if (xmlObj == null) {
            StringBuilder sb = new StringBuilder();
            XMLResource res = null;
            try {
                ExistDBContentHandler handler =
                    new ExistDBContentHandler(col, contentId, sb);

                res = (XMLResource)col.getResource(contentId);
                if (res instanceof RemoteXMLResource)
                    ((RemoteXMLResource)res).setLexicalHandler(new ExistDBContentLexicalHandler(
                        sb));
                else if (res instanceof LocalXMLResource)
                    ((LocalXMLResource)res).setLexicalHandler(new ExistDBContentLexicalHandler(
                        sb));

                res.getContentAsSAX(handler);
                xmlObj = XmlObject.Factory.parse(sb.toString());
                return xmlObj.newCursor();
            } catch (XmlException e) {
            	// TODO: log exception
                e.printStackTrace();
            } catch (XMLDBException e) {
            	// TODO: log exception
                e.printStackTrace();
            } finally {
                if (res != null) {
                    try {
                        ((EXistResource)res).freeResources();
                    } catch (XMLDBException xe) {
                        xe.printStackTrace();
                    }
                }
            }
        } else {
            return xmlObj.newCursor();
        }
        return null;

    }

}

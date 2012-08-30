package gov.nist.scap.content.exist;

import java.util.HashMap;
import java.util.Map;

import org.exist.xmldb.LocalXMLResource;
import org.exist.xmldb.RemoteXMLResource;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

/**
 * The SAX content handler for reading decomposed entities from eXist-db
 * @author Adam Halbardier
 *
 */
public class ExistDBContentHandler implements ContentHandler {

    private Collection col;
    private Map<String, String> namespaceMap = new HashMap<String, String>();
    private int isFirstElement = 1;
    private boolean moveDownNs;

    private StringBuilder sb;

    /**
     * default constructor
     * @param col the collection
     * @param resourceId the resource id to process
     * @param sb the string builder buffer to add to
     */
    public ExistDBContentHandler(
            Collection col,
            StringBuilder sb,
            boolean moveDownNs) {
        this.col = col;
        this.sb = sb;
        this.moveDownNs = moveDownNs;
    }

    @Override
    public void characters(char[] arg0, int arg1, int arg2) throws SAXException {
        sb.append(arg0, arg1, arg2);
    }

    @Override
    public void endDocument() throws SAXException {

    }

    @Override
    public void endElement(String arg0, String arg1, String arg2)
            throws SAXException {
        if ("gov:nist:scap:content-repo".equals(arg0)
            && "xinclude".equals(arg1)) {
            return;
        }
        if (ExistDBContentStore.WRAPPER_ELEMENT.equals(arg1)) {
            return;
        }

        sb.append("</" + arg2 + ">");
    }

    @Override
    public void endPrefixMapping(String arg0) throws SAXException {

    }

    @Override
    public void ignorableWhitespace(char[] arg0, int arg1, int arg2)
            throws SAXException {
        characters(arg0, arg1, arg2);
    }

    @Override
    public void processingInstruction(String arg0, String arg1)
            throws SAXException {

    }

    @Override
    public void setDocumentLocator(Locator arg0) {
    }

    @Override
    public void skippedEntity(String arg0) throws SAXException {
    }

    @Override
    public void startDocument() throws SAXException {
    }

    @Override
    public void startElement(
            String arg0,
            String arg1,
            String arg2,
            Attributes arg3) throws SAXException {
        try {
            if (isFirstElement != 1) {
                isFirstElement = -1;
                if ("gov:nist:scap:content-repo".equals(arg0)
                    && "xinclude".equals(arg1)) {
                    String resId = arg3.getValue("resource-id");
                    XMLResource res = (XMLResource)col.getResource(resId);
                    if (res instanceof RemoteXMLResource) {
                        ((RemoteXMLResource)res).setLexicalHandler(new ExistDBContentLexicalHandler(
                            sb));
                    } else if (res instanceof LocalXMLResource) {
                        ((LocalXMLResource)res).setLexicalHandler(new ExistDBContentLexicalHandler(
                            sb));
                    }
                    namespaceMap.clear();
                    res.getContentAsSAX(new ExistDBContentHandler(
                        col,
                        sb,
                        false));
                } else {
                    sb.append("<" + arg2);
                    for (int i = 0, size = arg3.getLength(); i < size; i++) {
                        sb.append(" " + arg3.getQName(i) + "=\"" + arg3.getValue(i)
                            + "\"");
                    }
                    for (String prefix : namespaceMap.keySet()) {
                    	writeNS(sb, prefix, namespaceMap.get(prefix));
                    }
                    namespaceMap.clear();
                    sb.append(">");
                }
            } else {
                isFirstElement = 0;
            }
        } catch (XMLDBException e) {
            throw new SAXException(e);
        }

    }

    @Override
    public void startPrefixMapping(String arg0, String arg1)
            throws SAXException {
        if (!moveDownNs && isFirstElement == 0) {
        	return;
        }
        
        if (isFirstElement == 0) {
            namespaceMap.put(arg0, arg1);
        }
    }
    
    private void writeNS(StringBuilder sb, String prefix, String ns) {
        String separator = "";
        if (prefix.length() > 0)
            separator = ":";
        sb.append(" xmlns" + separator + prefix + "=\""
            + ns + "\"");

    }

}

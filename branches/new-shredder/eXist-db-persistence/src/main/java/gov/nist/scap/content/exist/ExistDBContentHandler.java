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

public class ExistDBContentHandler implements ContentHandler {

    private Collection col;
    private Map<String, String> namespaceMap = new HashMap<String, String>();
    private Map<String, Map<String, String>> rootNamespaces;
    private boolean freezeNS = false;
    private boolean isFirstElement = true;
    private String resourceId;

    private StringBuilder sb;

    public ExistDBContentHandler(
            Collection col,
            String resourceId,
            StringBuilder sb) {
        this.col = col;
        this.resourceId = resourceId;
        this.sb = sb;
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
            if (!isFirstElement) {
                freezeNS = false;
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
                        res.getId(),
                        sb));
                } else {
                    sb.append("<" + arg2);
                    for (int i = 0, size = arg3.getLength(); i < size; i++) {
                        sb.append(" " + arg3.getQName(i) + "=\"" + arg3.getValue(i)
                            + "\"");
                    }
                    Map<String, String> namespaces =
                        isFirstElement ? rootNamespaces.get(resourceId)
                            : namespaceMap;
                    for (String prefix : namespaces.keySet()) {
                        String separator = "";
                        if (prefix.length() > 0)
                            separator = ":";
                        sb.append(" xmlns" + separator + prefix + "=\""
                            + namespaceMap.get(prefix) + "\"");
                    }
                    namespaceMap.clear();
                    isFirstElement = false;
                    sb.append(">");
                }
            } else {
                isFirstElement = false;
                freezeNS = true;
            }
        } catch (XMLDBException e) {
            throw new SAXException(e);
        }

    }

    @Override
    public void startPrefixMapping(String arg0, String arg1)
            throws SAXException {
        if (!freezeNS)
            namespaceMap.put(arg0, arg1);
    }

}

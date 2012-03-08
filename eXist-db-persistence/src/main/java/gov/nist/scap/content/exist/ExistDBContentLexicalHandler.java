package gov.nist.scap.content.exist;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class ExistDBContentLexicalHandler implements LexicalHandler {

    private StringBuilder sb;

    public ExistDBContentLexicalHandler(StringBuilder sb) {
        this.sb = sb;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        sb.append("<!--");
        sb.append(ch, start, length);
        sb.append("-->");
    }

    @Override
    public void endCDATA() throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void endDTD() throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void endEntity(String name) throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startCDATA() throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startDTD(String name, String publicId, String systemId)
            throws SAXException {
        // TODO Auto-generated method stub

    }

    @Override
    public void startEntity(String name) throws SAXException {
        // TODO Auto-generated method stub

    }

}

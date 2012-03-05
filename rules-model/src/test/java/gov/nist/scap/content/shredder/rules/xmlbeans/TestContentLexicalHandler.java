package gov.nist.scap.content.shredder.rules.xmlbeans;

import java.io.IOException;
import java.io.OutputStream;

import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;

public class TestContentLexicalHandler implements LexicalHandler {

	private OutputStream os;
	private String encoding;

	public TestContentLexicalHandler(OutputStream os, String encoding) {
	    this.os = os;
	    this.encoding = encoding;
	}

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        try {
            os.write("<!--".getBytes(encoding));
            os.write(new String(ch,start,length).getBytes(encoding));
            os.write("-->".getBytes(encoding));
        } catch (IOException e) {
            throw new SAXException(e);
        }
        
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

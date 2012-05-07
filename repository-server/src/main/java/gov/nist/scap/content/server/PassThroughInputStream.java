package gov.nist.scap.content.server;

import java.io.IOException;
import java.io.InputStream;

public class PassThroughInputStream extends InputStream {
    
    private InputStream is;
    private byte[] bArray;
    private int length;
    private int location = 0;
    
    public PassThroughInputStream(InputStream is, byte[] bArray, int length) {
        this.is = is;
        this.bArray = bArray;
        this.length = length;
    }
    
    @Override
    public int read() throws IOException {
    	if( location < length ) {
    		return bArray[location++];
    	} else {
    		return is.read();
    	}
    }

}

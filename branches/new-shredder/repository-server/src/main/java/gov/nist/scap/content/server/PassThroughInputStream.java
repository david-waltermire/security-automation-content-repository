package gov.nist.scap.content.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;

public class PassThroughInputStream extends InputStream {
    
    private InputStream is;
    private BufferedWriter bw;
    
    public PassThroughInputStream(InputStream is, BufferedWriter bw) {
        this.is = is;
        this.bw = bw;
    }
    
    @Override
    public int read() throws IOException {
        int i = is.read();
        bw.write(Integer.toString(i) + "\n");
        bw.flush();
        return i;
    }

}

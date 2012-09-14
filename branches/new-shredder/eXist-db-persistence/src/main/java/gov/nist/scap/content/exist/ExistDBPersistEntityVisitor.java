package gov.nist.scap.content.exist;

import gov.nist.scap.content.model.IContentNode;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IGeneratedDocument;
import gov.nist.scap.content.model.IKeyedDocument;

import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlCursor.TokenType;
import org.apache.xmlbeans.XmlOptions;
import org.exist.xmldb.EXistResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

public class ExistDBPersistEntityVisitor implements IEntityVisitor {

	private Collection col;
    public static final String WRAPPER_ELEMENT = "abcdefghijklm";
    private Map<String, IEntity<?>> resultMap;
    private boolean success = true;
    private Logger log = LoggerFactory.getLogger(ExistDBPersistEntityVisitor.class);

	
	public ExistDBPersistEntityVisitor(Collection col, Map<String, IEntity<?>> resultMap) {
		this.col = col;
		this.resultMap = resultMap;
	}
	
	@Override
	public void visit(IContentNode entity) {
		persistEntity(entity);
		
	}
	
	@Override
	public void visit(IGeneratedDocument entity) {
		persistEntity(entity);
	}
	
	@Override
	public void visit(IKeyedDocument entity) {
		persistEntity(entity);
	}
	
	
	private void persistEntity(IEntity<?> ie) {
		//do not allow additional persisting if there's been an error
		if( !success )
			return;
		
        XMLResource res = null;
        String resId = null;

        try {
            res = (XMLResource)col.createResource(null, "XMLResource");
            XmlOptions xo = new XmlOptions();
            xo.setSaveOuter();
            XmlCursor xc = ie.getContentHandle().getCursor();
            TokenType tt = xc.toNextToken();
            StringBuilder sbNS = new StringBuilder();
            sbNS.append("<" + WRAPPER_ELEMENT);
            while (tt == TokenType.ATTR || tt == TokenType.NAMESPACE) {
                if (tt == TokenType.NAMESPACE) {
                    QName q = xc.getName();
                    String spacer = "";
                    if (q.getLocalPart() != null
                        && !q.getLocalPart().equals("")) {
                        spacer = ":" + q.getLocalPart();
                    }
                    sbNS.append(" xmlns" + spacer + "=\""
                        + q.getNamespaceURI() + "\"");
                }
                tt = xc.toNextToken();
            }
            sbNS.append(">");
            xc = ie.getContentHandle().getCursor();
            res.setContent(sbNS.toString() + xc.getObject().xmlText(xo)
                + "</" + WRAPPER_ELEMENT + ">");
            col.storeResource(res);
            resId = res.getId();
            resultMap.put(resId, ie);
            xc.removeXml();
            xc.beginElement("xinclude", "gov:nist:scap:content-repo");
            xc.insertAttributeWithValue("resource-id", resId);
        } catch (XMLDBException e) {
            // back out all inserted info
            for (String localResId : resultMap.keySet()) {
                try {
                    col.removeResource(col.getResource(localResId));
                } catch (XMLDBException e1) {
                    log.error(
                        "Error rolling back transaction. Database may have stale data!!!",
                        e);
                }
            }
            success = false;
            log.error("error persisting content", e);
        } finally {
            // dont forget to cleanup
            if (res != null) {
                try {
                    ((EXistResource)res).freeResources();
                } catch (XMLDBException xe) {
                    xe.printStackTrace();
                }
            }
        }

	}
}

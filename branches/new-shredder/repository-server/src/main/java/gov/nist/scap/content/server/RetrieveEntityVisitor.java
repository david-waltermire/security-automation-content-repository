package gov.nist.scap.content.server;

import gov.nist.scap.content.model.IContentNode;
import gov.nist.scap.content.model.IEntity;
import gov.nist.scap.content.model.IEntityVisitor;
import gov.nist.scap.content.model.IGeneratedDocument;
import gov.nist.scap.content.model.IKeyedDocument;

import java.util.LinkedList;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.cxf.jaxrs.impl.ResponseBuilderImpl;

public class RetrieveEntityVisitor implements IEntityVisitor {

	private List<IEntity<?>> list = new LinkedList<IEntity<?>>();
	
	public Response getResponse() {
		ResponseBuilder builder = new ResponseBuilderImpl();
		if( list.size() == 0 ) {
			builder.status(404);
			return builder.build();
		} else if( list.size() == 1 ) {
			builder.status(200);
			builder.entity(list.get(0).getContentHandle().getCursor()
					.getObject().newInputStream());
			builder.type(MediaType.APPLICATION_XML);
			return builder.build();
		} else {
			builder.status(300);
			StringBuilder responseSb = new StringBuilder();
			for (IEntity<?> ie : list) {
				responseSb.append(ie.getId() + "\n");
			}
			builder.entity(responseSb.toString());
			builder.type(MediaType.TEXT_PLAIN_TYPE);
			return builder.build();
		}
	}
	
	private void visit(IEntity<?> entity) {
		list.add(entity);
	}
	
	@Override
	public void visit(IContentNode entity) {
		visit((IEntity<?>)entity);
	}
	
	@Override
	public void visit(IGeneratedDocument entity) {
		visit((IEntity<?>)entity);
	}
	
	@Override
	public void visit(IKeyedDocument entity) {
		visit((IEntity<?>)entity);
	}
}

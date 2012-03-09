package gov.nist.scap.content.model;


public interface IEntityVisitor {
	void visit(IKeyedDocument entity);
	void visit(IGeneratedDocument entity);
	void visit(IContentNode entity);
}

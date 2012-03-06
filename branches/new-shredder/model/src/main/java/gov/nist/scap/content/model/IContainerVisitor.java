package gov.nist.scap.content.model;


public interface IContainerVisitor {
	void visit(IKeyedDocument document);
	void visit(IGeneratedDocument document);
	void visit(IContentNode entity);
}

package gov.nist.scap.content.shredder.model;


public interface IContainerVisitor {
	void visit(IIndexedDocument document);
	void visit(IGeneratedDocument document);
	void visit(IContentNode entity);
}

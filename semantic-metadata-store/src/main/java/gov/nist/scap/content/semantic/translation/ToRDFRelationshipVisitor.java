package gov.nist.scap.content.semantic.translation;

import gov.nist.scap.content.model.IBoundaryIdentifierRelationship;
import gov.nist.scap.content.model.ICompositeRelationship;
import gov.nist.scap.content.model.IKeyedRelationship;
import gov.nist.scap.content.model.IRelationshipVisitor;
import gov.nist.scap.content.model.definitions.IExternalIdentifier;
import gov.nist.scap.content.semantic.MetaDataOntology;

import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.vocabulary.RDF;
import org.openrdf.model.vocabulary.RDFS;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public class ToRDFRelationshipVisitor implements IRelationshipVisitor {

    private final ValueFactory valueFactory;
    private final MetaDataOntology ontology;
    private final EntityMetadataMap entityMetadataMap;
    private final URI entityResourceId;
    private final RepositoryConnection conn;
    private final Resource context;

    /**
     * the default constructor
     * 
     * @param valueFactory the value factory
     * @param ontology the ontology
     * @param entityMetadataMap a callback to get entity information
     * @param entityResourceId the owning entity resource Id
     * @param conn a repository connection
     * @param context the context of the owning entity of this relationship
     */

    public ToRDFRelationshipVisitor(
            ValueFactory valueFactory,
            MetaDataOntology ontology,
            EntityMetadataMap entityMetadataMap,
            URI entityResourceId,
            RepositoryConnection conn,
            Resource context) {
        this.valueFactory = valueFactory;
        this.ontology = ontology;
        this.entityMetadataMap = entityMetadataMap;
        this.entityResourceId = entityResourceId;
        this.conn = conn;
        this.context = context;
    }

    @Override
    public void visit(IBoundaryIdentifierRelationship relationship) {
        List<Statement> target = new LinkedList<Statement>();
        String relationshipId = relationship.getDefinition().getId();
        IExternalIdentifier externalIdentifier =
            relationship.getExternalIdentifier();
        String boundaryObjectValue = relationship.getValue();

        // TODO: Fix this later
        URI boundaryObjectURI =
            valueFactory.createURI(externalIdentifier.getNamespace() + "#"
                + boundaryObjectValue);
        target.add(valueFactory.createStatement(
            boundaryObjectURI,
            RDF.TYPE,
            ontology.BOUNDARY_OBJECT_CLASS.URI));
        target.add(valueFactory.createStatement(
            boundaryObjectURI,
            RDFS.LABEL,
            valueFactory.createLiteral(boundaryObjectValue)));
        target.add(valueFactory.createStatement(
            boundaryObjectURI,
            ontology.HAS_BOUNDARY_OBJECT_TYPE.URI,
            valueFactory.createLiteral(externalIdentifier.getId())));
        target.add(valueFactory.createStatement(
            boundaryObjectURI,
            ontology.HAS_BOUNDARY_OBJECT_VALUE.URI,
            valueFactory.createLiteral(boundaryObjectValue)));
        // assert this since inference may not be turned on
        target.add(valueFactory.createStatement(
            entityResourceId,
            ontology.HAS_BOUNDARY_OBJECT_RELATIONSHIP_TO.URI,
            boundaryObjectURI));
        target.add(valueFactory.createStatement(
            entityResourceId,
            ontology.findIndirectRelationshipURI(relationshipId),
            boundaryObjectURI));
        try {
            conn.add(target, context);
        } catch (RepositoryException e) {
            // TODO: Implement better error handling!!!
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(ICompositeRelationship relationship) {
        List<Statement> target = new LinkedList<Statement>();
        String relationshipId = relationship.getDefinition().getId();
        // assert this since inference may not be turned on
        target.add(valueFactory.createStatement(
            entityResourceId,
            ontology.HAS_COMPOSITE_RELATIONSHIP_TO.URI,
            entityMetadataMap.getResourceURI(relationship.getReferencedEntity())));
        // adding incomplete statement to be completed later
        target.add(valueFactory.createStatement(
            entityResourceId,
            ontology.findCompositeRelationshipURI(relationshipId),
            entityMetadataMap.getResourceURI(relationship.getReferencedEntity())));
        try {
            conn.add(target, context);
        } catch (RepositoryException e) {
            // TODO: Implement better error handling!!!
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(IKeyedRelationship relationship) {
        List<Statement> target = new LinkedList<Statement>();
        String relationshipId = relationship.getDefinition().getId();
        // assert this since inference may not be turned on
        target.add(valueFactory.createStatement(
            entityResourceId,
            ontology.HAS_KEYED_RELATIONSHIP_TO.URI,
            entityMetadataMap.getResourceURI(relationship.getReferencedEntity())));
        // adding incomplete statement to be completed later
        target.add(valueFactory.createStatement(
            entityResourceId,
            ontology.findKeyedRelationshipURI(relationshipId),
            entityMetadataMap.getResourceURI(relationship.getReferencedEntity())));
        try {
            conn.add(target, context);
        } catch (RepositoryException e) {
            // TODO: Implement better error handling!!!
            throw new RuntimeException(e);
        }
    }
}

package gov.nist.scap.content.semantic.aspectj;

import org.apache.log4j.Logger;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;

public aspect LogAssertions {

    private static final Logger log = Logger.getLogger(LogAssertions.class);

    before(Iterable<? extends Statement> statements, Resource[] contexts): call(void org.openrdf.repository.RepositoryConnection.add(Iterable<? extends Statement>, Resource...)) && args(statements, contexts) {
        if (log.isTraceEnabled()) {
            for (Statement s : statements) {
                logStatement(
                    s.getSubject(),
                    s.getPredicate(),
                    s.getObject(),
                    contexts.length > 0 ? contexts : s.getContext() != null
                        ? new Resource[] {
                            s.getContext()
                        } : new Resource[0]);
            }
        }
    }

    before(Resource subject, URI predicate, Value object, Resource[] contexts): call(void org.openrdf.repository.RepositoryConnection.add(Resource, URI, Value, Resource...)) && args(subject, predicate, object, contexts) {
        if (log.isTraceEnabled()) {
            logStatement(subject, predicate, object, contexts);
        }
    }

    before(Statement st, Resource[] contexts): call(void org.openrdf.repository.RepositoryConnection.add(Statement, Resource...)) && args(st, contexts) {
        if (log.isTraceEnabled()) {
            logStatement(
                st.getSubject(),
                st.getPredicate(),
                st.getObject(),
                st.getContext() != null ? new Resource[] {
                    st.getContext()
                } : new Resource[0]);
        }
    }

    private void logStatement(
            Resource subject,
            URI predicate,
            Value object,
            Resource[] contexts) {
        StringBuilder sb = new StringBuilder();
        if (contexts.length > 0) {
            sb.append("[");
            for (Resource r : contexts) {
                sb.append(r + ", ");
            }
            sb.delete(sb.length() - 2, sb.length());
            sb.append("] ");
        }
        sb.append(subject.stringValue() + " " + predicate.stringValue() + " "
            + object.stringValue());
        log.trace(sb.toString());
    }

}

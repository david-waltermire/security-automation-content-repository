package gov.nist.scap.content.semantic.exceptions;

/**
 * Thrown when a triple store query expects only 1 result, but more than 1 comes
 * back
 * 
 * @author Adam Halbardier
 */
public class NonUniqueResultException extends RuntimeException {

    private static final long serialVersionUID = -7092645040241605667L;

    // CHECKSTYLE:OFF

    public NonUniqueResultException() {
        super();
    }
}

package gov.nist.scap.content.model.util;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assume.assumeThat;

import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

/**
 * Test the contract of {@link java.lang.Object#equals} and
 * {@link java.lang.Object#hashCode()} as defined in the Java Specification and
 * Javadoc.
 * 
 * This approach was inspired by the example at {@link http://stackoverflow.com/questions/837484/junit-theory-for-hashcode-equals-contract stackoverflow.com} posted
 * by {@link http://stackoverflow.com/users/89266/dfa dfa}. This implementation
 * differs from the before mentioned due to the assertion of equals as
 * implemented by {@link #evalEquals(Object, Object)} which further checks that
 * datapoints are being verified if they are expected to be equal.
 */
@Ignore
@RunWith(Theories.class)
public class ObjectEqualsHashCodeTest {
	protected static final int CONSISTENT_ATTEMPTS = 10;

	/**
	 * Test if the two parameters should be equal.  It is expected that
	 * subclasses will override this to check a set of
	 * {@link org.junit.experimental.theories.DataPoint} instances to determine
	 * if they are actually equal.
	 * @param x a non-null reference
	 * @param y a non-null reference
	 * @return <code>true</code> if the two parameters are equal or <code>false</code> if not.
	 */
	protected static boolean evalEquals(Object x, Object y) {
		return x.equals(y);
	}

	/**
	 * Test the reflexive aspect of the {@link java.lang.Object#equals(Object)} contract.
	 * @param x a non-null reference
	 */
	@SuppressWarnings("static-method")
	@Theory(nullsAccepted=false)
	public void equalsIsReflexive(Object x) {
//		// for any non-null reference value x
//		assumeThat(x, is(not(equalTo(null))));

		// x.equals(x) should return true.
		assertThat(x.equals(x), is(true));
	}

	/**
	 * Test the symmetric aspect of the {@link java.lang.Object#equals(Object)}
	 * contract.
	 * @param x a non-null reference
	 * @param y a non-null reference
	 */
	@SuppressWarnings("static-method")
	@Theory(nullsAccepted=false)
	public void equalsIsSymmetric(Object x, Object y) {
//
//		// for any non-null reference values x and y
//		assumeThat(x, is(not(equalTo(null))));
//		assumeThat(y, is(not(equalTo(null))));

		// test the datapoint to determine if it applies
		assumeThat(evalEquals(x, y), is(true));

		// x.equals(y) should return true if and only if y.equals(x) returns true. 
		assertThat(x.equals(y), is(true));
		assertThat(y.equals(x), is(true));
	}

	/**
	 * Test the transitive aspect of the {@link java.lang.Object#equals(Object)}
	 * contract.
	 * @param x a non-null reference
	 * @param y a non-null reference
	 * @param z a non-null reference
	 */
	@SuppressWarnings("static-method")
	@Theory(nullsAccepted=false)
	public void equalsIsTransitive(Object x, Object y, Object z) {
//		// for any non-null reference values x, y, and z
//		assumeThat(x, is(not(equalTo(null))));
//		assumeThat(y, is(not(equalTo(null))));
//		assumeThat(z, is(not(equalTo(null))));

		// test the datapoints to determine if they apply
		assumeThat(evalEquals(x, y) && evalEquals(y, z), is(true));

		// if x.equals(y) returns true and y.equals(z) returns true, then x.equals(z) should return true.
		assertThat(z.equals(x), is(true));
	}

	/**
	 * Test the consistent aspect of the {@link java.lang.Object#equals(Object)}
	 * contract.
	 * @param x a non-null reference
	 * @param y a non-null reference
	 */
	@SuppressWarnings("static-method")
	@Theory
	public void equalsIsConsistent(Object x, Object y) {
		// for any non-null reference values x and y
		assumeThat(x, is(not(equalTo(null))));

		// multiple invocations of x.equals(y) consistently return true or
		// consistently return false, provided no information used in equals
		// comparisons on the objects is modified. 
		boolean theSame = x.equals(y);
		for (int i = 0; i < CONSISTENT_ATTEMPTS; i++) {
			assertThat(x.equals(y), is(theSame));
		}
	}

	/**
	 * Test the "return false on null argument" aspect of the
	 * {@link java.lang.Object#equals(Object)} contract.
	 * @param x a non-null reference
	 */
	@SuppressWarnings("static-method")
	@Theory
	public void equalsReturnFalseOnNullArgument(Object x) {
//		// for any non-null reference value x
//		assumeThat(x, is(not(equalTo(null))));

		// x.equals(null) should return false.
		assertThat(x.equals(null), is(false));
	}
			
	/**
	 * Validate the contract "which states that equal objects must have equal hash codes."
	 * @param x a non-null reference
	 * @param y a non-null reference
	 */
	@SuppressWarnings("static-method")
	@Theory
	public void equalsIsConsistentWithHashCode(Object x, Object y) {
		// for any non-null reference value x
		assumeThat(x, is(not(equalTo(null))));
	
		// also implies that y is not null see equalsReturnFalseOnNullArgument
		assumeThat(x.equals(y), is(true));
		assertThat(x.hashCode(), is(equalTo(y.hashCode())));
	}

    /**
     * Test that when hashCode "is invoked on the same object more than once
     * during an execution of a Java application, the hashCode method must
     * consistently return the same integer, provided no information used in
     * equals comparisons on the object is modified."
	 * @param x a non-null reference
     */
	// Whenever it is invoked on the same object more than once
	// the hashCode() method must consistently return the same
	// integer.
    @SuppressWarnings("static-method")
	@Theory
	public void hashCodeIsConsistent(Object x) {
		// for any non-null reference value x
		assumeThat(x, is(not(equalTo(null))));

		int theSame = x.hashCode();
		for (int i = 0; i < CONSISTENT_ATTEMPTS; i++) {
			assertThat(x.hashCode(), is(theSame));
		}
	}

	// TODO: P4: add assertion for hash collision
	// It is not required that if two objects are unequal according to the
	// equals(java.lang.Object) method, then calling the hashCode method on each
	// of the two objects must produce distinct integer results. However, the
	// programmer should be aware that producing distinct integer results for
	// unequal objects may improve the performance of hashtables.
}

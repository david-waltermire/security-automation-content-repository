package gov.nist.scap.content.model;

import gov.nist.scap.content.model.util.ObjectEqualsHashCodeTest;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@Ignore
@RunWith(Theories.class)
public abstract class AbstractIKeyTest extends ObjectEqualsHashCodeTest {

	@SuppressWarnings("static-method")
	@Theory(nullsAccepted=false)
	public void idIsNotNull(IKey key) {
		Assert.assertNotNull(key.getId());
	}
//
//	@Test
//	public void testGetFieldNames() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetValue() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetValues() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testGetFieldNameToValueMap() {
//		fail("Not yet implemented");
//	}
//
//	@Test
//	public void testCompareTo() {
//		fail("Not yet implemented");
//	}
//
}

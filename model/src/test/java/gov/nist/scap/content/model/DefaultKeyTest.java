package gov.nist.scap.content.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;

public class DefaultKeyTest extends AbstractIKeyTest {
	@DataPoints
	public static DefaultKey[] points = generateDataPoints();

	private static DefaultKey[] generateDataPoints() {
		List<DefaultKey> data = new LinkedList<DefaultKey>();

		try {
			data.add(new DefaultKey("key1", new String[] { "test1" }, new String[] { "value1" }));
			data.add(new DefaultKey("key1", Collections.singletonList("test1"), Collections.singletonList("value1")));
			data.add(new DefaultKey("key1", new String[] { "test1" }, new String[] { "value1a" }));
			data.add(new DefaultKey("key2", new String[] { "test2" }, new String[] { "value2" }));
			data.add(new DefaultKey("key2", Collections.singletonList("test2"), Collections.singletonList("value2")));
			data.add(new DefaultKey("key2", new String[] { "test2" }, new String[] { "value2a" }));
			data.add(new DefaultKey("key3", new String[] { "test1", "test2" }, new String[] { "value1", "value2" }));
			data.add(new DefaultKey("key3", new String[] { "test1", "test2" }, new String[] { "value1", "value2" }));
			data.add(new DefaultKey("key4", new String[] { "test2", "test1" }, new String[] { "value2", "value1" }));

		} catch (KeyException e) {
			e.printStackTrace();
		}

		return data.toArray(new DefaultKey[data.size()]);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNullId() throws KeyException {
		boolean exceptionOccured = false;
		try {
			new DefaultKey(null, Collections.singletonList("test1"), Collections.singletonList("value1"));
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);

		try {
			new DefaultKey(null, new String[] { "test1" }, new String[] { "value1" });
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);

		try {
			new DefaultKey(null, new LinkedHashMap<String, String>(Collections.singletonMap("test1", "value1")));
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNullKeys() throws KeyException {
		boolean exceptionOccured = false;
		try {
			new DefaultKey("keyxyz", null, new String[] { "valuea" });
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);

		exceptionOccured = false;
		try {
			new DefaultKey("keyxyz", null, Collections.singletonList("valuea"));
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);

		exceptionOccured = false;
		try {
			new DefaultKey("keyxyz", null);
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);

		exceptionOccured = false;
		try {
			new DefaultKey("keyxyz", new LinkedHashMap<String, String>(Collections.<String, String>singletonMap(null, "valuea")));
		} catch (KeyException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);
	}

	@SuppressWarnings("static-method")
	@Test
	public void testNullValues() throws KeyException {
		boolean exceptionOccured = false;
		try {
			new DefaultKey("keyxyz", new String[] { "testa" }, null);
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);

		exceptionOccured = false;
		try {
			new DefaultKey("keyxyz", Collections.singletonList("testa"), null);
		} catch (NullPointerException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);

		exceptionOccured = false;
		try {
			new DefaultKey("keyxyz", new LinkedHashMap<String, String>(Collections.<String, String>singletonMap("valuea", null)));
		} catch (KeyException e) {
			exceptionOccured = true;
		}
		Assert.assertTrue(exceptionOccured);
	}
}

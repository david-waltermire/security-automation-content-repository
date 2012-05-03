package gov.nist.sparql.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.ValueFactory;

public class TripplesBlock {
	public static TripplesBlock tripples(Tripple...tripples) {
		return new TripplesBlock(tripples);
	}

	private final List<Tripple> tripples = new LinkedList<Tripple>();

	public TripplesBlock(Tripple...tripples) {
		this(Arrays.asList(tripples));
	}

	public TripplesBlock(List<Tripple> tripples) {
		if (tripples == null) {
			throw new NullPointerException("tripples");
		} else if (tripples.isEmpty()) {
			throw new IllegalArgumentException("tripples is empty");
		}
		this.tripples.addAll(tripples);
	}

	public List<Tripple> getTripples() {
		return Collections.unmodifiableList(tripples);
	}

	public void addTripple(Tripple tripple) {
		tripples.add(tripple);
	}

	public void buildQueryString(ValueFactory vf, StringBuilder builder) {
		for (Tripple tripple : tripples) {
			tripple.buildQueryString(vf, builder);
		}
	}
}

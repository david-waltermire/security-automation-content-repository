package gov.nist.sparql.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.openrdf.model.ValueFactory;

public class Tripple {
	public static Tripple tripple(IVarOrTerm varOrTerm, IProperty...properties) {
		return new Tripple(varOrTerm, properties);
	}

	private final IVarOrTerm varOrTerm;
	private final List<IProperty> properties = new LinkedList<IProperty>();

	public Tripple(IVarOrTerm varOrTerm, IProperty...properties) {
		this(varOrTerm, Arrays.asList(properties));
	}

	public Tripple(IVarOrTerm varOrTerm, List<IProperty> properties) {
		if (varOrTerm == null) {
			throw new NullPointerException("varOrTerm");
		}

		if (properties == null) {
			throw new NullPointerException("properties");
		} else if (properties.isEmpty()) {
			throw new IllegalArgumentException("properties is empty");
		}
		this.varOrTerm = varOrTerm;
		this.properties.addAll(properties);
	}

	public IVarOrTerm getVarOrTerm() {
		return varOrTerm;
	}

	public List<IProperty> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	public void addProperty(IProperty property) {
		properties.add(property);
	}

	public void buildQueryString(ValueFactory vf, StringBuilder builder) {
		builder.append(varOrTerm.getValue(vf));
		builder.append(' ');

		boolean first = true;
		for (IProperty property : properties) {

			if (first) {
				first = false;
			} else {
				builder.append(';');
				builder.append(SystemUtils.LINE_SEPARATOR);
				builder.append("  ");
			}

			builder.append(property.getVerb().getValue(vf));
			for (IObject obj : property.getObjects()) {
				builder.append(' ');
				builder.append(obj.getValue(vf));
			}
		}
		builder.append(" .");
		builder.append(SystemUtils.LINE_SEPARATOR);
	}
}

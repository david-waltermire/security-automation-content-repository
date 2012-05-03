package gov.nist.sparql.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.openrdf.model.ValueFactory;

public class SelectQueryBuilder extends AbstractSparqlBuilder {
	public enum Option {
		DISTINCT,
		REDUCED;
	}

	private Option option;
	private List<IVar> vars = Collections.emptyList();
	private GroupGraph where;

	public SelectQueryBuilder() {
	}

	public Option getOption() {
		return option;
	}

	public SelectQueryBuilder setOption(Option option) {
		this.option = option;
		return this;
	}

	public SelectQueryBuilder setSelectVars(IVar...vars) {
		this.vars = Arrays.asList(vars);
		return this;
	}

	public GroupGraph where() {
		where = new GroupGraph();
		return where;
	}

	@Override
	public void buildQueryString(ValueFactory vf, StringBuilder builder) {
		
		builder.append("SELECT");

		if (option != null) {
			builder.append(" ");
			builder.append(option.toString());
		}

		if (vars.isEmpty()) {
			builder.append(" *");
		} else {
			for (IVar var : vars) {
				builder.append(" ");
				builder.append(var.getValue(vf));
			}
		}
		builder.append(SystemUtils.LINE_SEPARATOR);
		if (where != null) {
			builder.append("WHERE");
			
			where.buildQueryString(vf, builder);
		}
//		newLine();
//		queryText.append("WHERE {");
//		newLine();
//		queryText.append("?entityURI a <http://scap.nist.gov/resource/content/model#entity> .");
//		newLine();
//		queryText.append("}");
//		newLine();
	}

}

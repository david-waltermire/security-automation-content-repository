package gov.nist.sparql.builder;

import java.net.URI;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SystemUtils;
import org.apache.log4j.Logger;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;

public abstract class AbstractSparqlBuilder {
	private static final Logger log = Logger.getLogger(AbstractSparqlBuilder.class);

	private static final String PN_CHARS_BASE = "[A-Z]|[a-z]|[\u00C0-\u00D6]|[\u00D8-\u00F6]|[\u00F8-\u02FF]|[\u0370-\u037D]|[\u037F-\u1FFF]|[\u200C-\u200D]|[\u2070-\u218F]|[\u2C00-\u2FEF]|[\u3001-\uD7FF]|[\uF900-\uFDCF]|[\uFDF0-\uFFFD]|[\u10000-\uEFFFF]";
	private static final String PN_CHARS_U = PN_CHARS_BASE+"|_";
	private static final String PN_CHARS = PN_CHARS_U+"|-|[0-9]|\u00B7|[\u0300-\u036F]|[\u203F-\u2040]";

	public static final Pattern PN_PREFIX = Pattern.compile("("+PN_CHARS_BASE+")(("+PN_CHARS+"|\\.)*("+PN_CHARS+"))?");
	public static final Pattern IRI_REF = Pattern.compile("[^<>\"{}|^`\\]\\-\\[\\x00-\\x20]*");
	public static final Pattern VARNAME = Pattern.compile("("+PN_CHARS_U+"|[0-9])("+PN_CHARS_U+"|[0-9]|\u00B7|[\u0300-\u036F]|[\u203F-\u2040])*");

	private final Map<URI, String> prefixMap = new LinkedHashMap<URI, String>();
	private final List<Prefix> prefixes = new LinkedList<Prefix>();
	private final Map<String, Value> bindingMap = new LinkedHashMap<String, Value>();
	private final Map<String, Integer> varMap = new HashMap<String, Integer>();

	private URI base;
	private Map<String, IVar> vars = new LinkedHashMap<String, IVar>();

	public AbstractSparqlBuilder() {
	}

	public URI getBase() {
		return base;
	}

	public AbstractSparqlBuilder setBase(URI base) {
		if (!IRI_REF.matcher(base.toASCIIString()).matches()) {
			throw new RuntimeException("Invalid BASE URI: "+base);
		}
		this.base = base;
		return this;
	}

	public Var newVar(String name) {
		if (!VARNAME.matcher(name).matches()) {
			throw new RuntimeException("Illegal var name: "+name);
		}
		if (vars.containsKey(name)) {
			throw new RuntimeException("Variable already registered: "+name);
		}
		Var var = new Var(name);
		this.vars.put(name, var);
		return var;
	}

	public IVar newVarUsingBaseName(String nameBase) {
		Integer currentValue = varMap.get(nameBase);
		if (currentValue == null) {
			currentValue = Integer.valueOf(1);
		}

		String name = nameBase+currentValue.toString();

		varMap.put(nameBase, currentValue);

		return newVar(name);
	}

	public IPrefix addPrefix(URI uri, String label) {
		if (prefixMap.containsKey(uri)) {
			throw new RuntimeException("The prefix mapping already contains the URI: "+uri);
		}
		if (prefixMap.containsValue(label)) {
			throw new RuntimeException("The prefix mapping already contains the label: "+label);
		}
		if (!IRI_REF.matcher(uri.toASCIIString()).matches()) {
			throw new RuntimeException("Invalid URI: "+uri);
		}
		if (label != null && !PN_PREFIX.matcher(label).matches()) {
			throw new RuntimeException("Invalid label: "+label);
		}

		prefixMap.put(uri, label);
		Prefix prefix = new Prefix(uri, label);
		prefixes.add(prefix);
		return prefix;
	}


	public StringURIResource createURI(String uri) {
		return new StringURIResource(uri);
	}

	public StringLiteral createLiteral(String contentId) {
		return new StringLiteral(contentId);
	}

	private String buildQueryString(RepositoryConnection conn) {
		StringBuilder builder = new StringBuilder();

		if (base != null) {
			builder.append("BASE <");
			builder.append(base.toASCIIString());
			builder.append('>');
			builder.append(SystemUtils.LINE_SEPARATOR);
		}

		if (!prefixMap.isEmpty()) {
			for (Prefix prefix : prefixes) {
				// OUTPUT: 'PREFIX' PNAME_NS IRI_REF
				// PNAME_NS = PN_PREFIX? ':'
				builder.append("PREFIX ");

				String label = prefix.getLabel();
				if (label != null) {
					builder.append(label);
				}
				builder.append(':');
				builder.append(' ');
				builder.append('<');
				builder.append(prefix.getUri().toASCIIString());
				builder.append('>');
				builder.append(SystemUtils.LINE_SEPARATOR);
			}
		}
		buildQueryString(conn.getValueFactory(), builder);
		return builder.toString();
	}

	protected abstract void buildQueryString(ValueFactory vf, StringBuilder builder);

	public TupleQuery build(RepositoryConnection conn) throws RepositoryException, MalformedQueryException {
		String queryString = buildQueryString(conn);

		log.info("SPARQL:\n"+queryString);

		TupleQuery tupleQuery = conn.prepareTupleQuery(QueryLanguage.SPARQL, queryString);
		if (!bindingMap.isEmpty()) {
//			ValueFactory vf = conn.getValueFactory();
			for (Map.Entry<String, Value> entry : bindingMap.entrySet()) {
				tupleQuery.setBinding(entry.getKey(), entry.getValue());
			}
		}
		return tupleQuery;
	}
}

package gov.nist.sparql.builder;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.SystemUtils;
import org.openrdf.model.ValueFactory;

// contains: '{' TriplesBlock? ( ( GraphPatternNotTriples | Filter ) '.'? TriplesBlock? )* '}'
// TriplesBlock 	  ::=   	TriplesSameSubject ( '.' TriplesBlock? )?
// TriplesSameSubject 	  ::=   	VarOrTerm PropertyListNotEmpty | TriplesNode PropertyList
// VarOrTerm 	  ::=   	Var | GraphTerm
// Var 	  ::=   	VAR1 | VAR2
// VAR1 	  ::=   	'?' VARNAME
// VAR2 	  ::=   	'$' VARNAME
// GraphTerm 	  ::=   	IRIref | RDFLiteral | NumericLiteral | BooleanLiteral | BlankNode | NIL
// PropertyListNotEmpty 	  ::=   	Verb ObjectList ( ';' ( Verb ObjectList )? )*
// Verb 	  ::=   	VarOrIRIref | 'a'
// ObjectList 	  ::=   	Object ( ',' Object )*
// 
public class GroupGraph implements IGroupGraph {
	public static GroupGraph group() {
		return new GroupGraph();
	}

	private final List<Statement> statements = new LinkedList<Statement>();

	public GroupGraph() {
	}

	@Override
	public IGroupGraph addStatement(TripplesBlock... blocks) {
		Statement statement = new Statement(null, null, Arrays.asList(blocks));
		statements.add(statement);
		return this;
	}

	@Override
	public IGroupGraph addStatement(IGraphPatternNotTripples pattern) {
		Statement statement = new Statement(null, pattern, Collections.<TripplesBlock>emptyList());
		statements.add(statement);
		return this;
	}

	@Override
	public IGroupGraph addStatement(IGraphPatternNotTripples pattern,
			TripplesBlock... blocks) {
		Statement statement = new Statement(null, pattern, Arrays.asList(blocks));
		statements.add(statement);
		return this;
	}

	@Override
	public IGroupGraph addStatement(TripplesBlock block,
			IGraphPatternNotTripples pattern, TripplesBlock... blocks) {
		Statement statement = new Statement(block, pattern, Arrays.asList(blocks));
		statements.add(statement);
		return this;
	}

	public void buildQueryString(ValueFactory vf, StringBuilder builder) {
		builder.append(" {");
		builder.append(SystemUtils.LINE_SEPARATOR);
		for (Statement statement : statements) {
			statement.buildQueryString(vf, builder);
		}
		builder.append('}');
		builder.append(SystemUtils.LINE_SEPARATOR);
	}
}

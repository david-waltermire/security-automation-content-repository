package org.scapdev.content.core.query;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Conditional<CONTEXT extends IContext<CONTEXT>> implements IConditional<CONTEXT> {

	public static <T extends IContext<T>> Conditional<T> allOf(IConstruct<T>... constructs) {
		return new Conditional<T>(Form.CONJUNCTIVE, constructs);
	}

	public static <T extends IContext<T>> Conditional<T> allOf(IConstruct<T> construct) {
		Conditional<T> retval = new Conditional<T>(Form.CONJUNCTIVE);
		retval.addConstruct(construct);
		return retval;
	}

	public static <T extends IContext<T>> Conditional<T> allOf(IConstruct<T> construct1, IConstruct<T> construct2) {
		Conditional<T> retval = new Conditional<T>(Form.CONJUNCTIVE);
		retval.addConstruct(construct1);
		retval.addConstruct(construct2);
		return retval;
	}

	public static <T extends IContext<T>> Conditional<T> allOf(IConstruct<T> construct1, IConstruct<T> construct2, IConstruct<T> construct3) {
		Conditional<T> retval = new Conditional<T>(Form.CONJUNCTIVE);
		retval.addConstruct(construct1);
		retval.addConstruct(construct2);
		retval.addConstruct(construct3);
		return retval;
	}

	public static <T extends IContext<T>> Conditional<T> allOf(IConstruct<T> construct1, IConstruct<T> construct2, IConstruct<T> construct3, IConstruct<T> construct4) {
		Conditional<T> retval = new Conditional<T>(Form.CONJUNCTIVE);
		retval.addConstruct(construct1);
		retval.addConstruct(construct2);
		retval.addConstruct(construct3);
		retval.addConstruct(construct4);
		return retval;
	}

	public static <T extends IContext<T>> Conditional<T> anyOf(IConstruct<T>... constructs) {
		return new Conditional<T>(Form.DISJUNCTIVE, constructs);
	}

	public static <T extends IContext<T>> Conditional<T> anyOf(IConstruct<T> construct) {
		Conditional<T> retval = new Conditional<T>(Form.DISJUNCTIVE);
		retval.addConstruct(construct);
		return retval;
	}

	public static <T extends IContext<T>> Conditional<T> anyOf(IConstruct<T> construct1, IConstruct<T> construct2) {
		Conditional<T> retval = new Conditional<T>(Form.DISJUNCTIVE);
		retval.addConstruct(construct1);
		retval.addConstruct(construct2);
		return retval;
	}

	public static <T extends IContext<T>> Conditional<T> anyOf(IConstruct<T> construct1, IConstruct<T> construct2, IConstruct<T> construct3) {
		Conditional<T> retval = new Conditional<T>(Form.DISJUNCTIVE);
		retval.addConstruct(construct1);
		retval.addConstruct(construct2);
		retval.addConstruct(construct3);
		return retval;
	}

	public static <T extends IContext<T>> Conditional<T> anyOf(IConstruct<T> construct1, IConstruct<T> construct2, IConstruct<T> construct3, IConstruct<T> construct4) {
		Conditional<T> retval = new Conditional<T>(Form.DISJUNCTIVE);
		retval.addConstruct(construct1);
		retval.addConstruct(construct2);
		retval.addConstruct(construct3);
		retval.addConstruct(construct4);
		return retval;
	}


	private final Form form;
	private final List<IConstruct<CONTEXT>> constructs;

	public Conditional(Form form) {
		this(form, new LinkedList<IConstruct<CONTEXT>>());
	}

	public Conditional(Form form, IConstruct<CONTEXT>... constructs) {
		this(form, new LinkedList<IConstruct<CONTEXT>>(Arrays.asList(constructs)));
	}

	@JsonCreator
	public Conditional(@JsonProperty("form") Form form, @JsonProperty("constructs") List<IConstruct<CONTEXT>> constructs) {
		this.form = form;
		this.constructs = constructs;
	}

	public void addConstruct(IConstruct<CONTEXT> construct) {
		constructs.add(construct);
	}

	public Form getForm() {
		return form;
	}

	public List<IConstruct<CONTEXT>> getConstructs() {
		return constructs;
	}

	@Override
	public <RESULT> RESULT visit(IQueryVisitor<RESULT, CONTEXT> visitor) {
		return visitor.visit(this);
	}
}

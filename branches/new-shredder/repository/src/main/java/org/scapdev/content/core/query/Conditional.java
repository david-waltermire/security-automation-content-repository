package org.scapdev.content.core.query;

import java.util.Arrays;
import java.util.List;

public class Conditional<CONSTRUCT extends IConstruct> implements IConditional<CONSTRUCT> {

	public static <T extends IConstruct> Conditional<T> allOf(T... constructs) {
		return new Conditional<T>(Form.CONJUNCTIVE, constructs);
	}

	public static <T extends IConstruct> Conditional<T> anyOf(T... constructs) {
		return new Conditional<T>(Form.DISJUNCTIVE, constructs);
	}

	public enum Form {
		CONJUNCTIVE, // AND
		DISJUNCTIVE; // OR
	}

	private final Form form;
	private final List<CONSTRUCT> constructs;

	public Conditional(Form form, CONSTRUCT... constructs) {
		this.form = form;
		this.constructs = Arrays.asList(constructs);
	}

	public Form getForm() {
		return form;
	}

	public List<CONSTRUCT> getConstructs() {
		return constructs;
	}
}

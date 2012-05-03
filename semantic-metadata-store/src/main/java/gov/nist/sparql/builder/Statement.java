package gov.nist.sparql.builder;

import java.util.List;

import org.openrdf.model.ValueFactory;

class Statement {
	private final TripplesBlock primaryTripplesBlock;
	private final IGraphPatternNotTripples pattern;
	private final List<TripplesBlock> secondaryTripplesBlocks;

	public Statement(TripplesBlock block, IGraphPatternNotTripples pattern,
			List<TripplesBlock> blocks) {
		if (blocks == null) {
			throw new NullPointerException("blocks");
		}

		this.primaryTripplesBlock = block;
		this.pattern = pattern;
		this.secondaryTripplesBlocks = blocks;
	}

	public void buildQueryString(ValueFactory vf, StringBuilder builder) {
		if (primaryTripplesBlock != null) {
			primaryTripplesBlock.buildQueryString(vf, builder);
		}

		if (pattern != null) {
			pattern.buildQueryString(vf, builder);
		}

		if (!secondaryTripplesBlocks.isEmpty()) {
			for (TripplesBlock block : secondaryTripplesBlocks) {
				block.buildQueryString(vf, builder);
			}
		}
	}

}

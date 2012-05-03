package gov.nist.sparql.builder;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.ValueFactory;

public class Union implements IGraphPatternNotTripples {
	public static Union union(IGroupGraph...groupGraphs) {
		return new Union(groupGraphs);
	}

	private final List<IGroupGraph> groupGraphs = new LinkedList<IGroupGraph>();

	public Union(IGroupGraph...groupGraphs) {
		this(Arrays.asList(groupGraphs));
	}

	public Union(List<IGroupGraph> groupGraphs) {
		this.groupGraphs.addAll(groupGraphs);
	}

	public void addGroupGraph(IGroupGraph groupGraph) {
		this.groupGraphs.add(groupGraph);
	}

	@Override
	public void buildQueryString(ValueFactory vf, StringBuilder builder) {

		boolean bFirst = true;
		for (IGroupGraph groupGraph : groupGraphs) {
			if (bFirst) {
				bFirst = false;
			} else {
				builder.append(" UNION ");
			}
			groupGraph.buildQueryString(vf, builder);
		}
	}

}

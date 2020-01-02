package phases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.impl.ElkNodeImpl;
import org.eclipse.emf.common.util.EList;

import helper.Help;

public class BinaryTreeCheckPhase implements Phase {

	@Override
	public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
		EList<ElkNode> nodes = layoutGraph.getChildren();

		if (hasCycle(layoutGraph))
			throw new Exception("Nobody told me I had to layout cyclic graphs D:");
		for (ElkNode n : nodes)
			if (Help.getChilds(n).size() > 2)
				throw new Exception(
						"Node " + n.getIdentifier() + " has more than 2 child nodes which is pretty unbinary!");
		ArrayList<ElkNode> groots = (ArrayList<ElkNode>) nodes.stream().filter(x -> x.getIncomingEdges().size() == 0)
				.collect(Collectors.toList());
		if (groots.size() != 1)
			throw new Exception(
					"Here it's I am gRoot and not we are gRoot! (A binary tree only has one root but this one has "
							+ groots.stream().map(x -> x.getIdentifier()).reduce((x, y) -> x + ", " + y) + ")");
	}

	boolean hasCycle(ElkNode layoutGraph) {
		for (ElkNode node : layoutGraph.getChildren()) {
			if (!Help.getProp(node).visiting && hasCycle(layoutGraph, node)) {
				return true;
			}
		}
		return false;
	}

	boolean hasCycle(ElkNode layoutGraph, ElkNode sourceNode) {
		Help.getProp(sourceNode).visiting = true;

		for (ElkEdge e : sourceNode.getOutgoingEdges())
			for (ElkConnectableShape n : e.getTargets())
				if (n.getClass().equals(ElkNodeImpl.class)) {
					if (Help.getProp((ElkNode) n).visiting)
						return true;
					else if (Help.getProp((ElkNode) n).visited && hasCycle(layoutGraph, (ElkNode) n))
						return true;
				}

		Help.getProp(sourceNode).visiting = false;
		Help.getProp(sourceNode).visited = true;
		return false;
	}
}

package phases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.EList;

import graph.drawing.RTProject.GraphState;
import graph.drawing.RTProject.GraphStatesManager;
import graph.drawing.RTProject.Options;
import helper.Graph;
import helper.Help;

public class RTLayoutPhaseSubtreeLayering implements Phase {
	double minSep = Options.SPACING_NODE_NODE;
	GraphStatesManager states;
	ElkNode root;
	ElkNode layoutGraph;
	List<List<ElkNode>> layers;

	public RTLayoutPhaseSubtreeLayering(GraphStatesManager states) {
		this.states = states;
	}

	public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
		EList<ElkNode> nodes = layoutGraph.getChildren();
		double nodeNodeSpacing = Options.SPACING_NODE_NODE;
		ElkPadding padding = Options.PADDING;

		root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
		this.layoutGraph = layoutGraph;

		layers = new ArrayList<List<ElkNode>>();
		int depth = Help.depth(root);
		for (int i = 0; i < depth; i++)
			layers.add(new ArrayList<ElkNode>());

		for (ElkNode n : nodes)
			layers.get(Help.rootDistance(n, root)).add(n);

		phase1(root);
		states.addState(new GraphState("Phase 1: Done!", Graph.fromElk(layoutGraph)));

		Help.getProp(root).xOffset = phase2(root) + padding.left;
		states.addState(new GraphState("Phase 2: Done!", Graph.fromElk(layoutGraph)));

		phase3(root, root.getX(), 0, nodeNodeSpacing, padding);
		states.addState(new GraphState("Phase 3: Done!", Graph.fromElk(layoutGraph)));
	}

	void phase1(ElkNode n) {
		List<ElkNode> childs = Help.getChilds(n);
		final ElkNode leftChild, rightChild;
		if (childs.size() > 0)
			leftChild = childs.get(0);
		else
			leftChild = null;
		if (childs.size() > 1)
			rightChild = childs.get(1);
		else
			rightChild = null;
		
		for (ElkNode c : childs)
			phase1(c);

		double dv = minSep * 2;
		if (leftChild != null)
			dv += leftChild.getWidth();
		if (leftChild != null)
			Help.getProp(leftChild).xOffset = -minSep;
		if (rightChild != null)
			Help.getProp(rightChild).xOffset = minSep;
		
		List<ElkNode> leftContour = new ArrayList<ElkNode>(), rightContour = new ArrayList<ElkNode>();
		if (leftChild != null && rightChild != null) {
			List<ElkNode> leftSubtree = Help.getSubtree(leftChild);
			List<ElkNode> rightSubtree = Help.getSubtree(rightChild);
			
			int minDepth = Math.min(Help.depth(rightChild), Help.depth(leftChild));
			for (int i = 0; i < minDepth; i++) {
				final int f = i;
				List<ElkNode> leftSubtreeLayer = leftSubtree.stream().
						filter(x -> Help.rootDistance(x, leftChild) == f).collect(Collectors.toList());
				List<ElkNode> rightSubtreeLayer = rightSubtree.stream().
						filter(x -> Help.rootDistance(x, rightChild) == f).collect(Collectors.toList());
				
				Double leftSubtreeLayerRightmostNumber = leftSubtreeLayer.stream().map(x -> Help.getProp(x).xOffset).max(Double::compare).get();
				ElkNode leftSubtreeLayerRightmost = leftSubtreeLayer.stream().filter(x -> Help.getProp(x).xOffset == leftSubtreeLayerRightmostNumber).findFirst().get();
				
				Double rightSubtreeLayerLeftmostNumber = rightSubtreeLayer.stream().map(x -> Help.getProp(x).xOffset).min(Double::compare).get();
				ElkNode rightSubtreeLayerLeftmost = rightSubtreeLayer.stream().filter(x -> Help.getProp(x).xOffset == rightSubtreeLayerLeftmostNumber).findFirst().get();
				
				Double leftSubtreeLayerRightmostTotalX = Help.xOffsetRT(leftSubtreeLayerRightmost, n) + leftSubtreeLayerRightmost.getWidth();
				Double rightSubtreeLayerLeftmostTotalX = Help.xOffsetRT(rightSubtreeLayerLeftmost, n);
				
				if (n.getIdentifier().contentEquals("n1"))
					getClass();
				
				if (leftSubtreeLayerRightmostTotalX + minSep > rightSubtreeLayerLeftmostTotalX - minSep)
					dv = leftSubtreeLayerRightmostTotalX - rightSubtreeLayerLeftmostTotalX + minSep * 2;
				
				if (-dv / 2 < Help.getProp(leftChild).xOffset)
					Help.getProp(leftChild).xOffset = -dv / 2;
				if (dv / 2 > Help.getProp(rightChild).xOffset)
					Help.getProp(rightChild).xOffset = dv / 2;
				
				leftContour.add(leftSubtreeLayerRightmost);
				rightContour.add(rightSubtreeLayerLeftmost);
				
				states.addState(new GraphState("Phase 1, Postorder: Set offsets of childs of " + n.getIdentifier() + 
						" | Check difference of " + leftSubtreeLayerRightmost.getIdentifier() + " and " + rightSubtreeLayerLeftmost.getIdentifier(),
						Graph.fromElk(layoutGraph), n, Help.concat(leftContour, rightContour)));
			} 
		} else
			states.addState(new GraphState("Phase 1, Postorder: Visit " + n.getIdentifier(),
					Graph.fromElk(layoutGraph), n));
	}

	double phase2(ElkNode r) {
		double re = 0.0;
		while (Help.getChilds(r).size() > 0) {
			states.addState(new GraphState("Phase 2, get total X position of the root: " + re,
					Graph.fromElk(layoutGraph), r));
			
			re += Help.getProp(r).xOffset + r.getWidth() / 2;
			r = Help.getChilds(r).get(0);
		}
		states.addState(new GraphState("Phase 2, total X position of the root: " + re,
				Graph.fromElk(layoutGraph), r));
		return re;
	}

	void phase3(ElkNode r, double rootOffset, int depth, double nodeNodeSpacing, ElkPadding padding) {
		List<ElkNode> childs = Help.getChilds(r);
		ElkNode leftChild = null, rightChild = null;
		if (childs.size() > 0)
			leftChild = childs.get(0);
		if (childs.size() > 1)
			rightChild = childs.get(1);

		r.setX(Help.getProp(r).xOffset + rootOffset);
		r.setY(depth * (r.getHeight() + nodeNodeSpacing) + padding.top);

		states.addState(
				new GraphState("Phase 3, Preorder: Apply offset to " + r.getIdentifier(), Graph.fromElk(layoutGraph), r));

		for (ElkNode c : childs)
			phase3(c, r.getX(), depth + 1, nodeNodeSpacing, padding);
	}
}
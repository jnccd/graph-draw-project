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

public class RTLayoutPhaseWithThreads implements Phase {
	double minSep = Options.SPACING_NODE_NODE;
	GraphStatesManager states;
	ElkNode root;
	ElkNode layoutGraph;
	List<List<ElkNode>> layers;

	public RTLayoutPhaseWithThreads(GraphStatesManager states) {
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
		states.addState(new GraphState("Set offset of root " + root.getIdentifier(), Graph.fromElk(layoutGraph), root));
		states.addState(new GraphState("Phase 2: Done!", Graph.fromElk(layoutGraph)));

		phase3(root, root.getX(), 0, nodeNodeSpacing, padding);
		states.addState(new GraphState("Phase 3: Done!", Graph.fromElk(layoutGraph)));
	}

	void phase1(ElkNode n) {
		List<ElkNode> childs = Help.getChilds(n);
		ElkNode leftChild = null, rightChild = null;
		if (childs.size() > 0)
			leftChild = childs.get(0);
		if (childs.size() > 1)
			rightChild = childs.get(1);
		for (ElkNode c : childs)
			phase1(c);

		if (leftChild != null)
			Help.getProp(leftChild).xOffset = -minSep;
		if (rightChild != null)
			Help.getProp(rightChild).xOffset = minSep;

		double dv = minSep * 2;
		List<Double> lC = new ArrayList<Double>();
		List<Double> rC = new ArrayList<Double>();
		List<ElkNode> lCn = new ArrayList<ElkNode>();
		List<ElkNode> rCn = new ArrayList<ElkNode>();

		if (leftChild != null) {
			ElkNode clCn = leftChild;
			lCn.add(clCn);
			lC.add(Help.getProp(clCn).xOffset);
			while (Help.getChilds(clCn).size() > 1 || Help.getProp(clCn).rightThread != null) {
				if (Help.getProp(clCn).rightThread != null) {
					clCn = Help.getProp(clCn).rightThread;
				} else {
					clCn = Help.getChilds(clCn).get(1);
				}
				lCn.add(clCn);
				lC.add(Help.getProp(clCn).xOffset + lC.get(lC.size() - 1));
			}
		}

		if (rightChild != null) {
			ElkNode crCn = rightChild;
			rCn.add(crCn);
			rC.add(Help.getProp(crCn).xOffset);
			while (Help.getChilds(crCn).size() > 0 || Help.getProp(crCn).leftThread != null) {
				if (Help.getProp(crCn).leftThread != null) {
					crCn = Help.getProp(crCn).leftThread;
				} else {
					crCn = Help.getChilds(crCn).get(0);
				}
				rCn.add(crCn);
				rC.add(Help.getProp(crCn).xOffset + rC.get(rC.size() - 1));
			}
		}

		for (int i = 0; i < Math.min(lC.size(), rC.size()); i++) {
			if (n.getIdentifier().contentEquals("n3"))
				getClass();
			
			if (rC.get(i) - lC.get(i) - minSep - n.getWidth() + dv < 0)
				dv = lC.get(i) - rC.get(i) + n.getWidth() + minSep * 2;
		}

		// Center child if there is only one, not doing this would make
		// trees where the root only has 1 subtree look unbalanced/unsymmetric
		if (leftChild == null && rightChild != null)
			Help.getProp(rightChild).xOffset = 0;
		else if (leftChild != null && rightChild == null)
			Help.getProp(leftChild).xOffset = 0;
		else if (leftChild != null && rightChild != null) {
			Help.getProp(leftChild).xOffset = -dv / 2;
			Help.getProp(rightChild).xOffset = dv / 2;
		}

		if (leftChild != null && rightChild != null) {
			int lDepth = Help.depth(leftChild);
			int rDepth = Help.depth(rightChild);
			if (lDepth > rDepth) {

			}
		}

		List<ElkNode> contours = lCn;
		contours.addAll(rCn);
		states.addState(new GraphState("Postorder: Set offsets of childs of " + n.getIdentifier(),
				Graph.fromElk(layoutGraph), n, contours));
	}

	double phase2(ElkNode r) {
		double re = 0.0;
		while (Help.getChilds(r).size() > 0) {
			re += Help.getProp(r).xOffset + r.getWidth() / 2;
			r = Help.getChilds(r).get(0);
		}
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
				new GraphState("Preorder: Apply offset to " + r.getIdentifier(), Graph.fromElk(layoutGraph), r));

		for (ElkNode c : childs)
			phase3(c, r.getX(), depth + 1, nodeNodeSpacing, padding);
	}
}
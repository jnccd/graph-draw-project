package phases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.EList;

import graph.drawing.RTProject.GraphState;
import graph.drawing.RTProject.GraphStatesManager;
import graph.drawing.RTProject.Options;
import helper.Graph;
import helper.Help;

/**
 * This is a layout phase that layouts the graph using a RT implementation. 
 * However this phase is modified to additionally create GraphState instances of the 
 * current progress and add them to the GraphStatesManager.
 * @author dobiko
 */
public class RTLayoutPhase implements Phase {
	int minSep = 1;
	GraphStatesManager states;
	ElkNode root;
	ElkNode layoutGraph;

	public RTLayoutPhase(GraphStatesManager states) {
		this.states = states;
	}

	public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
		EList<ElkNode> nodes = layoutGraph.getChildren();

		root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
		this.layoutGraph = layoutGraph;

		phase1(root);
		states.addState(new GraphState("Phase 1: Done!", Graph.fromElk(layoutGraph)));

		root.setX(-phase2(root) + 1);
		states.addState(new GraphState("Phase 2: Done!", Graph.fromElk(layoutGraph)));

		phase3(root, root.getX(), 0);
		states.addState(new GraphState("Phase 3: Done!", Graph.fromElk(layoutGraph)));
	}

	void phase1(ElkNode n) {
		List<ElkNode> childs = Help.getChildren(n);
		for (ElkNode c : childs)
			phase1(c);

		final ElkNode leftChild, rightChild;
		leftChild = childs.size() > 0 ? childs.get(0) : null;
		rightChild = childs.size() > 1 ? childs.get(1) : null;
		
		// Set dv and xOffset to standard values
		int dv = minSep * 2;
		if (leftChild != null)
			Help.getProp(leftChild).xOffset = -1;
		if (rightChild != null)
			Help.getProp(rightChild).xOffset = 1;
		
		// If both child nodes exist check their contours
		if (leftChild != null && rightChild != null) {
			Pair<List<ElkNode>, List<ElkNode>> contour = getContourUsingSubtreeLayering(leftChild, rightChild);
			List<ElkNode> leftContour = contour.getFirst(), rightContour = contour.getSecond();
			
			// Add the animation state if enabled
			if (!Options.hideContourStates)
				states.addState(new GraphState("Phase 1, Postorder: Set offsets of childs of " + n.getIdentifier(),
						Graph.fromElk(layoutGraph), n, Help.concat(leftContour, rightContour)));
			
			for (int i = 0; i < leftContour.size(); i++) {
				Integer leftSubtreeLayerRightmostTotalX = Help.xOffsetRT(leftContour.get(i), n);
				Integer rightSubtreeLayerLeftmostTotalX = Help.xOffsetRT(rightContour.get(i), n);
				
				// Set the dv to a higher value in case of contour intersections
				if (leftSubtreeLayerRightmostTotalX + minSep > rightSubtreeLayerLeftmostTotalX - minSep &&
					dv < leftSubtreeLayerRightmostTotalX - rightSubtreeLayerLeftmostTotalX + 2 + minSep * 2)
					dv = leftSubtreeLayerRightmostTotalX - rightSubtreeLayerLeftmostTotalX + 2 + minSep * 2;
				
				// Update xOffsets to the new dv value
				Help.getProp(leftChild).xOffset = -dv / 2;
				Help.getProp(rightChild).xOffset = dv / 2;
				
				// Add the animation state if enabled
				if (!Options.hideContourDifferenceStates)
					states.addState(new GraphState(
							"Phase 1, Postorder: Set offsets of childs of " + n.getIdentifier()
									+ " | Check difference of " + leftContour.get(i).getIdentifier() + " and "
									+ rightContour.get(i).getIdentifier(),
							Graph.fromElk(layoutGraph), n, Help.concat(leftContour, rightContour), leftContour.get(i),
							rightContour.get(i), leftSubtreeLayerRightmostTotalX, rightSubtreeLayerLeftmostTotalX, dv));
			}
			
			addThreads(rightChild, leftChild);
		} else
			states.addState(
					new GraphState("Phase 1, Postorder: Visit " + n.getIdentifier(), Graph.fromElk(layoutGraph), n));
	}

	Pair<List<ElkNode>, List<ElkNode>> getContourUsingSubtreeLayering(ElkNode leftChild, ElkNode rightChild) {
		Pair<List<ElkNode>, List<ElkNode>> re = new Pair<List<ElkNode>, List<ElkNode>>();
		List<ElkNode> leftContour = new ArrayList<ElkNode>(), rightContour = new ArrayList<ElkNode>();
		re.setFirst(leftContour);
		re.setSecond(rightContour);

		// Get the whole subtrees...
		List<ElkNode> leftSubtree = Help.getSubtree(leftChild);
		List<ElkNode> rightSubtree = Help.getSubtree(rightChild);

		int minDepth = Math.min(Help.depth(rightChild), Help.depth(leftChild));
		for (int i = 0; i < minDepth; i++) {
			final int f = i;
			List<ElkNode> leftSubtreeLayer = leftSubtree.stream().filter(x -> Help.rootDistance(x, leftChild) == f)
					.collect(Collectors.toList());
			List<ElkNode> rightSubtreeLayer = rightSubtree.stream().filter(x -> Help.rootDistance(x, rightChild) == f)
					.collect(Collectors.toList());
			
			// ... and find the right/leftmost Node in each layer using mighty java streams!
			Integer leftSubtreeLayerRightmostNumber = leftSubtreeLayer.stream().map(x -> Help.getProp(x).xOffset)
					.max(Double::compare).get();
			ElkNode leftSubtreeLayerRightmost = leftSubtreeLayer.stream()
					.filter(x -> Help.getProp(x).xOffset == leftSubtreeLayerRightmostNumber).findFirst().get();

			Integer rightSubtreeLayerLeftmostNumber = rightSubtreeLayer.stream().map(x -> Help.getProp(x).xOffset)
					.min(Double::compare).get();
			ElkNode rightSubtreeLayerLeftmost = rightSubtreeLayer.stream()
					.filter(x -> Help.getProp(x).xOffset == rightSubtreeLayerLeftmostNumber).findFirst().get();

			leftContour.add(leftSubtreeLayerRightmost);
			rightContour.add(rightSubtreeLayerLeftmost);
		}

		return re;
	}

	List<ElkNode> getSubtreeLayer(ElkNode n, int layer) {
		return Help.getSubtree(n).stream().filter(x -> Help.rootDistance(x, n) == layer).collect(Collectors.toList());
	}

	void addThreads(ElkNode rightChild, ElkNode leftChild) {
		// Find the left/rightmost nodes in the deepest layer of each subtree and link them
		// If the left one is deeper we need to take the rightmost nodes or the other way around
		int dR = Help.depth(rightChild);
		int dL = Help.depth(leftChild);
		List<ElkNode> lL = getSubtreeLayer(leftChild, dL - 1);
		List<ElkNode> lR = getSubtreeLayer(rightChild, dR - 1);
		if (dL > dR) {
			int maxL = lL.stream().map(x -> Help.xOffsetRT(x, leftChild)).max(Integer::compare).get();
			int maxR = lR.stream().map(x -> Help.xOffsetRT(x, rightChild)).max(Integer::compare).get();
			ElkNode maxLN = lL.stream().filter(x -> Help.xOffsetRT(x, leftChild) == maxL).reduce((a, b) -> b).get();
			ElkNode maxRN = lR.stream().filter(x -> Help.xOffsetRT(x, rightChild) == maxR).reduce((a, b) -> b).get();
			Help.getProp(maxRN).thread = maxLN;
		} else if (dR > dL) {
			int minL = lL.stream().map(x -> Help.xOffsetRT(x, leftChild)).min(Integer::compare).get();
			int minR = lR.stream().map(x -> Help.xOffsetRT(x, rightChild)).min(Integer::compare).get();
			ElkNode minLN = lL.stream().filter(x -> Help.xOffsetRT(x, leftChild) == minL).reduce((a, b) -> b).get();
			ElkNode minRN = lR.stream().filter(x -> Help.xOffsetRT(x, rightChild) == minR).reduce((a, b) -> b).get();
			Help.getProp(minLN).thread = minRN;
		}
	}

	int phase2(ElkNode r) {
		int re = 0;
		while (Help.getChildren(r).size() > 0) {
			states.addState(
					new GraphState("Phase 2, get total X position of the root: " + (-re + 1), Graph.fromElk(layoutGraph), r));

			re += Help.getProp(r).xOffset;
			r = Help.getChildren(r).get(0);
		}
		states.addState(new GraphState("Phase 2, total X position of the root: " + (-re + 1), Graph.fromElk(layoutGraph), r));
		return re;
	}

	void phase3(ElkNode r, double rootOffset, int depth) {
		List<ElkNode> childs = Help.getChildren(r);

		int offset = Help.getProp(r).xOffset;
		r.setX(offset + rootOffset);
		r.setY(Help.rootDistance(r, root));

		states.addState(new GraphState("Phase 3, Preorder: Apply offset to " + r.getIdentifier(),
				Graph.fromElk(layoutGraph), r));

		for (ElkNode c : childs)
			if (c != null)
				phase3(c, r.getX(), depth + 1);
	}
}
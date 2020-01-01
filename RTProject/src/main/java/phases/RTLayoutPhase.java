package phases;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.core.util.Pair;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.EList;

import graph.drawing.RTProject.GraphState;
import graph.drawing.RTProject.GraphStatesManager;
import graph.drawing.RTProject.Options;
import helper.Graph;
import helper.Help;

public class RTLayoutPhase implements Phase {
    double minSep = 25;
    GraphStatesManager states;
    ElkNode root;
    ElkNode layoutGraph;
    
    public RTLayoutPhase(GraphStatesManager states) {
    	this.states = states;
    }

    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        EList<ElkNode> nodes = layoutGraph.getChildren();
        double nodeNodeSpacing = Options.SPACING_NODE_NODE;
        ElkPadding padding = Options.PADDING;
        
        root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
        this.layoutGraph = layoutGraph;
        
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
        List<Pair<Double, ElkNode>> lC = new ArrayList<Pair<Double,ElkNode>>();
        List<Pair<Double, ElkNode>> rC = new ArrayList<Pair<Double,ElkNode>>();
        if (leftChild != null && rightChild != null) {
        	lC = getContour(leftChild, false);
            rC = getContour(rightChild, true);
            for (int i = 0; i < Math.min(lC.size(), rC.size()); i++) {
                if (rC.get(i).getFirst() - lC.get(i).getFirst() + dv < 0)
                    dv = lC.get(i).getFirst() - rC.get(i).getFirst() + minSep;
            }
        }
        
        // Center child if there is only one, not doing this would make 
        // trees where the root only has 1 subtree look unbalanced/unsymmetric
        if      (leftChild == null && rightChild != null)
            Help.getProp(rightChild).xOffset = 0;
        else if (leftChild != null && rightChild == null)
            Help.getProp(leftChild).xOffset = 0;
        else if (leftChild != null && rightChild != null) {
            Help.getProp(leftChild).xOffset = -dv / 2;
            Help.getProp(rightChild).xOffset = dv / 2;
        }
        
        List<ElkNode> contours = (List<ElkNode>) lC.stream().map(x -> x.getSecond()).collect(Collectors.toList());
        contours.addAll(rC.stream().map(x -> x.getSecond()).collect(Collectors.toList()));
        states.addState(new GraphState("Postorder: Set offsets of childs of " + n.getIdentifier(), 
        		Graph.fromElk(layoutGraph), n, contours));
    }
    
    List<Pair<Double, ElkNode>> getContour(ElkNode root, boolean left) { // Inefficient but it works
        if (root == null) {
        	ArrayList<Pair<Double, ElkNode>> l = new ArrayList<Pair<Double, ElkNode>>();
        	l.add(new Pair<Double, ElkNode>(0.0, root));
        	return l;
        }
        
        List<ElkNode> c = Help.getChilds(root);
        
        if (c.size() == 0){
        	ArrayList<Pair<Double, ElkNode>> l = new ArrayList<Pair<Double, ElkNode>>();
        	l.add(new Pair<Double, ElkNode>(0.0, root));
        	return l;
        }
        
        ElkNode lc = c.get(0);
        ElkNode rc = c.get(c.size() - 1);
        
        List<Pair<Double, ElkNode>> lC = getContour(lc, left);
        List<Pair<Double, ElkNode>> rC = getContour(rc, left);
        
        List<Pair<Double, ElkNode>> re = new ArrayList<Pair<Double,ElkNode>>(Math.max(lC.size(), rC.size()) + 1);
        re.add(new Pair<Double, ElkNode>(0.0, root));
        for (int i = 1; i < re.size(); i++) {
            if (left) {
            	if (lC.size() > i - 1 && 
            			lC.get(i - 1).getFirst() + Help.getProp(lc).xOffset < 
            			rC.get(i - 1).getFirst() + Help.getProp(rc).xOffset)
            		re.add(lC.get(i - 1));
            	else
            		re.add(rC.get(i - 1));
            }
            else{
            	if (lC.size() > i - 1 && 
            			lC.get(i - 1).getFirst() + Help.getProp(lc).xOffset > 
            			rC.get(i - 1).getFirst() + Help.getProp(rc).xOffset)
            		re.add(lC.get(i - 1));
            	else
            		re.add(rC.get(i - 1));
            }
        }
        
        return re;
    }
    
    double phase2(ElkNode r) {
        double re = 0.0;
        while (Help.getChilds(r).size() > 0) {
            re += Help.getProp(r).xOffset + r.getWidth() / 2;
            r = Help.getChilds(r).get(0);
        }
        return re;
    }
    
    void phase3(ElkNode r, double rootOffset, int depth, 
            double nodeNodeSpacing, ElkPadding padding) {
        List<ElkNode> childs = Help.getChilds(r);
        ElkNode leftChild = null, rightChild = null;
        if (childs.size() > 0) 
            leftChild = childs.get(0);
        if (childs.size() > 1) 
            rightChild = childs.get(1);
        
        r.setX(Help.getProp(r).xOffset + rootOffset);
        r.setY(depth * (r.getHeight() + nodeNodeSpacing) + padding.top);
        
        states.addState(new GraphState("Preorder: Apply offset to " + r.getIdentifier(), Graph.fromElk(layoutGraph), r));
        
        for (ElkNode c : childs)
            phase3(c, r.getX(), depth + 1, nodeNodeSpacing, padding);
    }
}
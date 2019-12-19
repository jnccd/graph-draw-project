package graph.drawing.RTProject;

import java.util.List;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.EList;

import helper.Help;

public class RTLayoutPhase {
    double minSep = 25;

    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        EList<ElkNode> nodes = layoutGraph.getChildren();
        double nodeNodeSpacing = Options.SPACING_NODE_NODE;
        ElkPadding padding = Options.PADDING;
        
        ElkNode root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
        
        phase1(root);
        root.setX(phase2(root) + padding.left);
        phase3(root, root.getX(), 0, nodeNodeSpacing, padding);
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
        
        double dv = minSep * 2, dl = 0, dr = 0;
        double[] lC = getContour(leftChild, false);
        double[] rC = getContour(rightChild, true);
        for (int i = 0; i < Math.min(lC.length, rC.length); i++) {
            if (rC[i] - lC[i] + dv < 0)
                dv = lC[i] - rC[i] + minSep;
        }
        
        // Center child if there is only one, not doing this would make 
        // trees where the root only has 1 subtree look unbalanced/unsymmetric
        if      (leftChild == null && rightChild != null)
            Help.getProp(rightChild).xOffset = 0;
        else if (leftChild != null && rightChild == null)
            Help.getProp(leftChild).xOffset = 0;
        else if (leftChild != null && rightChild != null) {
            Help.getProp(leftChild).xOffset = -dv / 2;
            Help.getProp(rightChild).xOffset = dv / 2;;
        }
    }
    
    double[] getContour(ElkNode root, boolean left) { // Inefficient but it works
        if (root == null)
            return new double[] { 0 };
        
        List<ElkNode> c = Help.getChilds(root);
        
        if (c.size() == 0)
            return new double[] { 0 };
        
        ElkNode lc = c.get(0);
        ElkNode rc = c.get(c.size() - 1);
        
        double[] lC = getContour(lc, left);
        double[] rC = getContour(rc, left);
        
        double[] re = new double[Math.max(lC.length, rC.length) + 1];
        re[0] = 0;
        for (int i = 1; i < re.length; i++) {
            if (left) {
                re[i] = Integer.MAX_VALUE;
                
                if (lC.length > i - 1)
                    re[i] = Math.min(lC[i - 1] + Help.getProp(lc).xOffset, re[i]);
                if (rC.length > i - 1)
                    re[i] = Math.min(rC[i - 1] + Help.getProp(rc).xOffset, re[i]);
            }
            else{
                re[i] = Integer.MIN_VALUE;
                
                if (lC.length > i - 1)
                    re[i] = Math.max(lC[i - 1] + Help.getProp(lc).xOffset + lc.getWidth(), re[i]);
                if (rC.length > i - 1)
                    re[i] = Math.max(rC[i - 1] + Help.getProp(rc).xOffset + rc.getWidth(), re[i]);
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
        
        for (ElkNode c : childs)
            phase3(c, r.getX(), depth + 1, nodeNodeSpacing, padding);
    }
}
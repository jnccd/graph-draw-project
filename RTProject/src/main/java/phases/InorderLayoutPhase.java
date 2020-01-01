package phases;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.EList;

import graph.drawing.RTProject.Options;
import helper.Help;

public class InorderLayoutPhase implements Phase {
    List<ElkNode> inorderVisiting = new ArrayList<ElkNode>();

    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        EList<ElkNode> nodes = layoutGraph.getChildren();
        double nodeNodeSpacing = Options.SPACING_NODE_NODE;
        ElkPadding padding = Options.PADDING;
        
        ElkNode root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
        inorder(root);
        
        double curX = padding.left;
        for (ElkNode n : inorderVisiting) {
            n.setX(curX);
            n.setY(rootDistance(n, root) * (n.getHeight() + nodeNodeSpacing) + padding.top);
            
            curX += n.getWidth() + nodeNodeSpacing;
        }
    }
    
    int rootDistance(ElkNode n, ElkNode root) {
        int re = 0;
        while (n != root) {
            n = Help.getParents(n).get(0);
            re++;
        }
        return re;
    }
    
    void inorder(ElkNode n) {
        List<ElkNode> childs = Help.getChilds(n);
        if (childs.size() > 0) {
            ElkNode leftChild = childs.get(0);
            inorder(leftChild);
        }
        
        inorderVisiting.add(n);
        
        if (childs.size() > 1) {
            ElkNode rightChild = childs.get(1);
            inorder(rightChild);
        }
    }
}

package phases;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.emf.common.util.EList;

import helper.Help;

/**
 * This is a Phase that layouts a graph using the inorder algorithm.
 * @author dobiko
 */
public class InorderLayoutPhase implements Phase {
    List<ElkNode> inorderVisiting = new ArrayList<ElkNode>();

    public void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception {
        EList<ElkNode> nodes = layoutGraph.getChildren();
        
        ElkNode root = nodes.stream().filter(x -> x.getIncomingEdges().size() == 0).findFirst().get();
        inorder(root);
        
        int curX = 0;
        for (ElkNode n : inorderVisiting) {
            n.setX(curX);
            n.setY(Help.rootDistance(n, root));
            
            curX += 1;
        }
    }
    
    void inorder(ElkNode n) {
        List<ElkNode> childs = Help.getChildren(n);
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

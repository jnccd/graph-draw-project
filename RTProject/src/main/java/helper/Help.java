package helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.common.util.EList;

/**
 * This class contains a lot of helpful miscellaneous methods.
 * @author dobiko
 */
public class Help {
	// General Helpers
	/**
	 * Concat two lists
	 * @param <T> The two List's type
	 * @param as List 1
	 * @param bs List 2
	 * @return Concatenated List
	 */
	public static <T> List<T> concat(List<T> as, List<T> bs) {
		List<T> rs = new ArrayList<T>();
		rs.addAll(as);
		rs.addAll(bs);
		return rs;
	}
	
	
	// Elk Helpers
	/**
	 * Get the depth of the elk tree
	 * @param root
	 * @return the depth of the elk tree
	 */
	public static int depth(ElkNode root) {
		List<ElkNode> childs = Help.getChildren(root);
		if (childs.size() == 0)
			return 1;
		else
			return childs.stream().map(x -> depth(x)).max(Integer::compare).get() + 1;
	}
	
	/**
	 * Get the distance from n to the root in an elk tree
	 * @param root
	 * @return the distance from n to the root in an elk tree
	 */
	public static int rootDistance(ElkNode n, ElkNode root) {
		if (n == root)
			return 0;
		ElkNode p = (ElkNode) n.getIncomingEdges().get(0).getSources().get(0);
		return rootDistance(p, root) + 1;
	}
	
	/**
	 * Sum the xOffset value from the RT algorithm from Node n to root
	 * @param n
	 * @param root
	 * @return Sum of the xOffset value from the RT algorithm from Node n to root
	 */
	public static int xOffsetRT(ElkNode n, ElkNode root) {
		if (n == root)
			return 0;
		ElkNode p = Help.getParents(n).get(0);
		return xOffsetRT(p, root) + Help.getProp(n).xOffset;
	}
	
	/**
	 * Get a list of all nodes in the subtree below and including n
	 * @param n
	 * @return A list of all nodes in the subtree below and including n
	 */
	public static List<ElkNode> getSubtree(ElkNode n) {
		if (Help.getChildren(n).size() == 0) {
			List<ElkNode> re = new ArrayList<ElkNode>();
			re.add(n);
			return re;
		}
		
		List<ElkNode> re = Help.getChildren(n).stream().map(x -> getSubtree(x)).reduce((x, y) -> {
			List<ElkNode> ree = new ArrayList<ElkNode>();
			ree.addAll(x);
			ree.addAll(y);
			return ree;
		}).get();
		re.add(n);
		return re;
	}
	
	/**
	 * Get the child nodes of n within a tree
	 * @param n
	 * @return the child nodes of n within a tree
	 */
	public static List<ElkNode> getChildren(ElkNode n) {
        EList<ElkEdge> outs = n.getOutgoingEdges();
        List<ElkNode> re = new ArrayList<ElkNode>();
        for (ElkEdge out : outs) {
            for (ElkConnectableShape target : out.getTargets())
                if (ElkNode.class.isAssignableFrom(target.getClass())) {
                    re.add((ElkNode)target);
                }
        }
        re = re.stream().distinct().collect(Collectors.toList());
        return re;
    }
	
	/**
	 * Get the parent nodes of n within a tree
	 * @param n
	 * @return the parent nodes of n within a tree
	 */
    public static List<ElkNode> getParents(ElkNode n) {
        EList<ElkEdge> outs = n.getIncomingEdges();
        List<ElkNode> re = new ArrayList<ElkNode>();
        for (ElkEdge out : outs) {
            for (ElkConnectableShape target : out.getSources())
                if (ElkNode.class.isAssignableFrom(target.getClass())) {
                    re.add((ElkNode)target);
                }
        }
        re = re.stream().distinct().collect(Collectors.toList());
        return re;
    }
    
	
	// Property Getter
    /**
     * Get a pointer to the NodeProperty instance of e
     * @param e
     * @return a pointer to the NodeProperty instance of e
     */
    public static NodeProperty getProp(ElkNode e) {
        Property<NodeProperty> prop = new Property<NodeProperty>("prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new NodeProperty());
        return e.getProperty(prop);
    }
}

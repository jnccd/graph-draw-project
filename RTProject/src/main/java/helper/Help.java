package helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.properties.Property;
import org.eclipse.emf.common.util.EList;

import properties.EdgeProperty;
import properties.GraphProperty;
import properties.NodeProperty;

public class Help {
	public static <T> List<T> concat(List<T> as, List<T> bs) {
		List<T> rs = new ArrayList<T>();
		rs.addAll(as);
		rs.addAll(bs);
		return rs;
	}
	
	
	// Elk Helpers
	public static int depth(ElkNode root) {
		List<ElkNode> childs = Help.getChilds(root);
		if (childs.size() == 0)
			return 1;
		else
			return childs.stream().map(x -> depth(x)).max(Integer::compare).get() + 1;
	}

	public static int rootDistance(ElkNode n, ElkNode root) {
		if (n == root)
			return 0;
		ElkNode p = (ElkNode) n.getIncomingEdges().get(0).getSources().get(0);
		return rootDistance(p, root) + 1;
	}
	
	public static double xOffsetRT(ElkNode n, ElkNode root) {
		if (n == root)
			return 0;
		ElkNode p = Help.getParents(n).get(0);
		return xOffsetRT(p, root) + Help.getProp(n).xOffset;
	}

	public static List<ElkNode> getSubtree(ElkNode n) {
		if (Help.getChilds(n).size() == 0) {
			List<ElkNode> re = new ArrayList<ElkNode>();
			re.add(n);
			return re;
		}
		
		List<ElkNode> re = Help.getChilds(n).stream().map(x -> getSubtree(x)).reduce((x, y) -> {
			List<ElkNode> ree = new ArrayList<ElkNode>();
			ree.addAll(x);
			ree.addAll(y);
			return ree;
		}).get();
		re.add(n);
		return re;
	}
	
	public static List<ElkNode> getChilds(ElkNode n) {
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
	
	// Properties
    public static NodeProperty getProp(ElkNode e) {
        Property<NodeProperty> prop = new Property<NodeProperty>("prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new NodeProperty());
        return e.getProperty(prop);
    }
    public static EdgeProperty getProp(ElkEdge e) {
        Property<EdgeProperty> prop = new Property<EdgeProperty>("prop");
        
        if (e.hasProperty(prop))
            return e.getProperty(prop);
        
        e.setProperty(prop, new EdgeProperty());
        return e.getProperty(prop);
    }
    public static GraphProperty getGraphProp(ElkNode n) {
        Property<GraphProperty> prop = new Property<GraphProperty>("graph-prop");
        
        if (n.hasProperty(prop))
            return n.getProperty(prop);
        
        n.setProperty(prop, new GraphProperty());
        return n.getProperty(prop);
    }
}

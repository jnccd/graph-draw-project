package helper;

import java.util.List;

import org.eclipse.elk.graph.ElkEdge;

public class Edge {
    public Edge(List<Node> sources, List<Node> targets, ElkEdge parent) {
        super();
        this.sources = sources;
        this.targets = targets;
        this.parent = parent;
    }

    public List<Node> sources;
    public List<Node> targets;
    
    public ElkEdge parent;
}

package helper;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

public class Node {
    public Node(List<Edge> incoming, List<Edge> outgoing, ElkNode parent) {
        super();
        this.incoming = incoming;
        this.outgoing = outgoing;
        this.parent = parent;
    }

    public List<Edge> incoming;
    public List<Edge> outgoing;

    public ElkNode parent;
}

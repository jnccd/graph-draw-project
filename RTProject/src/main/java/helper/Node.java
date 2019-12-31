package helper;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

public class Node {
	double x,y,w,h;
	
	public List<Edge> incoming;
    public List<Edge> outgoing;

    public ElkNode parent;

	public Node(double x, double y, double w, double h, List<Edge> incoming, List<Edge> outgoing, ElkNode parent) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.incoming = incoming;
		this.outgoing = outgoing;
		this.parent = parent;
	}
}

package helper;

import java.util.List;

import org.eclipse.elk.graph.ElkNode;

/**
 * This class mirrors an ElkNode and additionally also contains a RT thread pointer.
 * All methods were generated by eclipse
 * @author dobiko
 */
public class Node {
	public double x, y, w, h;
	public String name, note;
	
	public List<Edge> incoming;
	public List<Edge> outgoing;

	public ElkNode parent, thread;

	public Node(double x, double y, double w, double h, String name, String note, List<Edge> incoming,
			List<Edge> outgoing, ElkNode parent) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.name = name;
		this.note = note;
		this.incoming = incoming;
		this.outgoing = outgoing;
		this.parent = parent;
	}
	
	public Node(double x, double y, double w, double h, String name, String note, List<Edge> incoming,
			List<Edge> outgoing, ElkNode parent, ElkNode thread) {
		super();
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.name = name;
		this.note = note;
		this.incoming = incoming;
		this.outgoing = outgoing;
		this.parent = parent;
		this.thread = thread;
	}

	public double getX() {
		return x;
	}
	public double getY() {
		return y;
	}
	public double getWidth() {
		return w;
	}
	public double getHeight() {
		return h;
	}
}

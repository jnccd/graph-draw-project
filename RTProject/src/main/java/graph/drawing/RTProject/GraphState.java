package graph.drawing.RTProject;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.List;

import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

import helper.Edge;
import helper.Graph;
import helper.Node;

public class GraphState {
	private String title;
	private Graph graph;
	private ElkNode markedNode;
	private List<ElkNode> contourNodes;

	public GraphState(String title, Graph g) {
		super();
		this.graph = g;
		this.title = title;
	}

	public GraphState(String title, Graph graph, ElkNode markedNode) {
		super();
		this.graph = graph;
		this.title = title;
		this.markedNode = markedNode;
	}

	public GraphState(String title, Graph graph, ElkNode markedNode, List<ElkNode> contourNodes) {
		super();
		this.title = title;
		this.graph = graph;
		this.markedNode = markedNode;
		this.contourNodes = contourNodes;
	}

	public String getTitle() {
		return title;
	}

	public void draw(Graphics g, Component target, MainFrame frame) {
		if (graph.nodes.size() == 0)
			return;

		// fix draw environment
		Double minX = graph.nodes.stream().map(x -> x.x).min(Double::compare).get();
		Double minY = graph.nodes.stream().map(x -> x.y).min(Double::compare).get();
		Double maxX = graph.nodes.stream().map(x -> x.x + x.w + 1).max(Double::compare).get();
		Double maxY = graph.nodes.stream().map(x -> x.y + x.h + 1).max(Double::compare).get();

		for (Node n : graph.nodes) {
			n.x = ((n.x - minX) + target.getWidth() / 2 - (maxX - minX) / 2);
			n.y = ((n.y - minY) * target.getHeight()) / (maxY - minY);
		}

		frame.setMinimumSize(new Dimension((int) (maxX - minX) + 10, 0));

		// draw
		for (Edge e : graph.edges) {
			Node src = e.sources.get(0);
			Node tar = e.targets.get(0);
			g.drawLine((int) src.x + (int) src.w / 2, (int) src.y + (int) src.h / 2, (int) tar.x + (int) tar.w / 2,
					(int) tar.y + (int) tar.h / 2);
		}

		for (Node n : graph.nodes) {
			boolean isContour = contourNodes != null && contourNodes.stream().filter(x -> x.getIdentifier().contentEquals(n.name)).findAny()
					.isPresent();

			g.drawRect((int) n.x, (int) n.y, (int) n.w, (int) n.h);
			if (markedNode != null && n.name.contentEquals(markedNode.getIdentifier()))
				g.setColor(Color.ORANGE);
			else if (contourNodes != null && isContour)
				g.setColor(Color.DARK_GRAY);
			else
				g.setColor(Color.CYAN);
			g.fillRect((int) n.x, (int) n.y, (int) n.w, (int) n.h);

			
			if (isContour)
				g.setColor(Color.WHITE);
			else
				g.setColor(Color.BLACK);
			g.drawString(n.name, (int) (n.x + (n.w - g.getFontMetrics().stringWidth(n.name)) / 2),
					(int) (n.y + g.getFontMetrics().getHeight()));

			g.drawString(n.note, (int) (n.x + (n.w - g.getFontMetrics().stringWidth(n.note)) / 2),
					(int) (n.y + g.getFontMetrics().getHeight() + 20));
		}
	}
}

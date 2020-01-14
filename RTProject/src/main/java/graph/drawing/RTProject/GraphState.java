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
	
	private Node leftArrowNode, rightArrowNode;
	private int leftArrowNumber, rightArrowNumber;

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

	public GraphState(String title, Graph graph, ElkNode markedNode, List<ElkNode> contourNodes, ElkNode leftArrowNode,
			ElkNode rightArrowNode, int leftArrowNumber, int rightArrowNumber) {
		super();
		this.title = title;
		this.graph = graph;
		this.markedNode = markedNode;
		this.contourNodes = contourNodes;
		this.leftArrowNumber = leftArrowNumber;
		this.rightArrowNumber = rightArrowNumber;
		
		this.leftArrowNode = graph.nodes.stream().
				filter(x -> x.name == leftArrowNode.getIdentifier()).findFirst().get();
		this.rightArrowNode = graph.nodes.stream().
				filter(x -> x.name == rightArrowNode.getIdentifier()).findFirst().get();
	}

	public String getTitle() {
		return title;
	}

	public void draw(Graphics g, Component target, MainFrame frame) {
		if (graph.nodes.size() == 0)
			return;

		fixDrawEnvironment(target, frame);
		
		// draw
		for (Edge e : graph.edges) {
			Node src = e.sources.get(0);
			Node tar = e.targets.get(0);
			
			int srcX, srcY, tarX, tarY;
			if (src.x < tar.x) {
				srcX = (int)(src.x + src.w);
				tarX = (int)tar.x;
			} else {
				srcX = (int)src.x;
				tarX = (int)(tar.x + tar.w);
			}
			
			if (src.y < tar.y) {
				srcY = (int)(src.y + src.h);
				tarY = (int)tar.y;
			} else {
				srcY = (int)src.y;
				tarY = (int)(tar.y + tar.h);
			}
			
			g.drawLine(srcX, srcY, tarX, tarY);
		}

		for (Node n : graph.nodes) {
			boolean isContour = contourNodes != null && contourNodes.stream().filter(x -> x.getIdentifier().contentEquals(n.name)).findAny()
					.isPresent();

			g.setColor(Color.BLACK);
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
		
		g.setColor(Color.BLACK);
		if (leftArrowNode != null && rightArrowNode != null) {
			g.drawLine(
					(int)(leftArrowNode.getX() + leftArrowNode.getWidth() + Options.SPACING_NODE_NODE / 4), 
					(int)(leftArrowNode.getY() + leftArrowNode.getHeight() / 2), 
					(int)(rightArrowNode.getX() - Options.SPACING_NODE_NODE / 4), 
					(int)(rightArrowNode.getY() + rightArrowNode.getHeight() / 2));
			g.drawString(Integer.toString(leftArrowNumber), 
					(int)(leftArrowNode.getX() + leftArrowNode.getWidth() + Options.SPACING_NODE_NODE / 2), 
					(int)(leftArrowNode.getY() + leftArrowNode.getHeight() / 2 - 5));
			g.drawString(Integer.toString(rightArrowNumber), 
					(int)(rightArrowNode.getX() - Options.SPACING_NODE_NODE), 
					(int)(leftArrowNode.getY() + leftArrowNode.getHeight() / 2 - 5));
		}
	}
	private void fixDrawEnvironment(Component target, MainFrame frame) {
		Double minX = graph.nodes.stream().map(x -> x.x).min(Double::compare).get();
		Double minY = graph.nodes.stream().map(x -> x.y).min(Double::compare).get();
		Double maxX = graph.nodes.stream().map(x -> x.x + x.w + 1).max(Double::compare).get();
		Double maxY = graph.nodes.stream().map(x -> x.y + x.h + 1).max(Double::compare).get();

		for (Node n : graph.nodes) {
			n.x = ((n.x - minX) + target.getWidth() / 2 - (maxX - minX) / 2);
			n.y = ((n.y - minY) + target.getHeight() / 2 - (maxY - minY) / 2);
		}
		
		int minSizeX = (int) (maxX - minX) + (int)Options.PADDING.left + 
				(int)Options.PADDING.right + (frame.getWidth() - target.getWidth());
		int minSizeY = (int) (maxY - minY) + (int)Options.PADDING.top + 
				(int)Options.PADDING.bottom + (frame.getHeight() - target.getHeight());
		if (minSizeX > 1920)
			minSizeX = 1920;
		if (minSizeY > 900)
			minSizeY = 900;
		frame.setMinimumSize(new Dimension(minSizeX, minSizeY));
	}
}

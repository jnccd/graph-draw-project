package graph.drawing.RTProject;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.util.List;

import javax.swing.text.StyleConstants.FontConstants;

import org.eclipse.elk.core.math.ElkPadding;
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
		
		ElkPadding pad = Options.PADDING;
		
		// Set the gridSize so that the entire tree + padding fits on the screen
		int gridSize = (int)Math.min((target.getWidth() - pad.getHorizontal()) * 0.9 / 
											(graph.nodes.stream().map(x -> x.x).max(Double::compare).get() + 1), 
								     (target.getHeight() - pad.getVertical()) * 0.9 / 
								     		(graph.nodes.stream().map(x -> x.y).max(Double::compare).get() + 1));
		
		for (Node n : graph.nodes) {
			n.w = gridSize * 0.9;
			n.h = gridSize * 0.9;
		}
		
		// Draw the edges
		g.setColor(Color.BLACK);
		for (Edge e : graph.edges) {
			Node src = e.sources.get(0);
			Node tar = e.targets.get(0);
			
			int srcX, srcY, tarX, tarY;
			if (src.x < tar.x) {
				srcX = (int)(src.x * gridSize + src.w) + (int)pad.left;
				tarX = (int)tar.x * gridSize + (int)pad.left;
			} else {
				srcX = (int)src.x * gridSize + (int)pad.left;
				tarX = (int)(tar.x * gridSize + tar.w) + (int)pad.left;
			}
			
			if (src.y < tar.y) {
				srcY = (int)(src.y * gridSize + src.h) + (int)pad.top;
				tarY = (int)tar.y * gridSize + (int)pad.top;
			} else {
				srcY = (int)src.y * gridSize + (int)pad.top;
				tarY = (int)(tar.y * gridSize + tar.h) + (int)pad.top;
			}
			
			g.drawLine(srcX, srcY, tarX, tarY);
		}
		
		// Draw the nodes in the correct colours
		for (Node n : graph.nodes) {
			boolean isContour = contourNodes != null && contourNodes.stream().
					filter(x -> x.getIdentifier().contentEquals(n.name)).findAny()
					.isPresent();

			g.setColor(Color.BLACK);
			g.drawRect((int) n.x * gridSize + (int)pad.left, (int) n.y * gridSize + (int)pad.top, (int) n.w, (int) n.h);
			
			if (markedNode != null && n.name.contentEquals(markedNode.getIdentifier())) g.setColor(Color.ORANGE);
			else if (contourNodes != null && isContour) g.setColor(Color.DARK_GRAY);
			else g.setColor(Color.CYAN);
			g.fillRect((int) n.x * gridSize + (int)pad.left, (int) n.y * gridSize + (int)pad.top, (int) n.w, (int) n.h);

			if (isContour) g.setColor(Color.WHITE);
			else g.setColor(Color.BLACK);
			
			// Draw text
			if (n.h > 30) {
				g.setFont(new Font(frame.getStateLabel().getFont().getName(), Font.PLAIN, (int)(n.h * 0.4)));
				
				String name = new String(n.name);
				String note = new String(n.note);
				while (g.getFontMetrics().stringWidth(name) > n.w)
					name = name.substring(0, name.length() - 2);
				while (g.getFontMetrics().stringWidth(note) > n.w)
					note = note.substring(0, note.length() - 2);
				
				g.drawString(name, (int) (n.x * gridSize + (int)pad.left + (n.w - g.getFontMetrics().stringWidth(name)) / 2),
						(int) n.y * gridSize + (int)pad.top + g.getFontMetrics().getAscent() - (int)(n.h * 0.1));
				g.drawString(note, (int) (n.x * gridSize + (int)pad.left + (n.w - g.getFontMetrics().stringWidth(note)) / 2),
						(int) n.y * gridSize + (int)pad.top + g.getFontMetrics().getHeight() * 3 / 2 + (int)(n.h * 0.1));
			} else {
				g.setFont(new Font(frame.getStateLabel().getFont().getName(), Font.PLAIN, (int)(n.h * 0.9)));
				
				String name = new String(n.name);
				while (g.getFontMetrics().stringWidth(name) > n.w)
					name = name.substring(0, name.length() - 2);
				
				g.drawString(name, (int) (n.x * gridSize + (int)pad.left + (n.w - g.getFontMetrics().stringWidth(name)) / 2),
						(int) (n.y * gridSize + pad.top + g.getFontMetrics().getAscent() * 1.1 - 5));
			}
		}
		
		// Draw the contour difference arrows
		g.setColor(Color.BLACK);
		if (leftArrowNode != null && rightArrowNode != null) {
			g.drawLine(
					(int)(leftArrowNode.getX() * gridSize + (int)pad.left + leftArrowNode.getWidth() + Options.SPACING_NODE_NODE / 4), 
					(int)(leftArrowNode.getY() * gridSize + (int)pad.top + leftArrowNode.getHeight() / 2), 
					(int)(rightArrowNode.getX() * gridSize + (int)pad.left - Options.SPACING_NODE_NODE / 4), 
					(int)(rightArrowNode.getY() * gridSize + (int)pad.top + rightArrowNode.getHeight() / 2));
			g.drawString(Integer.toString(leftArrowNumber), 
					(int)(leftArrowNode.getX() * gridSize + (int)pad.left + leftArrowNode.getWidth() + Options.SPACING_NODE_NODE / 2), 
					(int)(leftArrowNode.getY() * gridSize + (int)pad.top + leftArrowNode.getHeight() / 2 - 5));
			g.drawString(Integer.toString(rightArrowNumber), 
					(int)(rightArrowNode.getX() * gridSize + (int)pad.left - Options.SPACING_NODE_NODE), 
					(int)(leftArrowNode.getY() * gridSize + (int)pad.top + leftArrowNode.getHeight() / 2 - 5));
		}
	}
}

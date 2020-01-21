package graph.drawing.RTProject;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.text.StyleConstants.FontConstants;

import org.eclipse.elk.core.math.ElkPadding;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

import helper.Edge;
import helper.Graph;
import helper.Help;
import helper.Node;

/**
 * This class holds all the information nessecary to visualise a state in the RT algorithm. 
 * The left and right arrow attributes may be confusing, they contain all the nessecary 
 * information to draw the line that symbolises the check of the contour difference.  
 * The class also contains the draw method which draws this state to a target component.
 * @author dobiko
 *
 */
public class GraphState {
	private String title;
	private Graph graph;
	private ElkNode markedNode;
	private List<ElkNode> contourNodes;

	private Node leftArrowNode, rightArrowNode;
	private int leftArrowNumber, rightArrowNumber;
	private int dv;

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
			ElkNode rightArrowNode, int leftArrowNumber, int rightArrowNumber, int dv) {
		super();
		this.title = title;
		this.graph = graph;
		this.markedNode = markedNode;
		this.contourNodes = contourNodes;
		this.leftArrowNumber = leftArrowNumber;
		this.rightArrowNumber = rightArrowNumber;
		this.dv = dv;

		this.leftArrowNode = graph.nodes.stream().filter(x -> x.name == leftArrowNode.getIdentifier()).findFirst()
				.get();
		this.rightArrowNode = graph.nodes.stream().filter(x -> x.name == rightArrowNode.getIdentifier()).findFirst()
				.get();
	}

	public String getTitle() {
		return title;
	}

	public String getMarkedNodeName() {
		if (markedNode != null)
			return markedNode.getIdentifier();
		else
			return "";
	}
	
	public List<String> getContourNodeNames() {
		if (contourNodes != null)
			return contourNodes.stream().map(x -> x.getIdentifier()).collect(Collectors.toList());
		else
			return new ArrayList<String>();
	}

	public void draw(Graphics g, Component target, MainFrame frame) {
		if (graph.nodes.size() == 0)
			return;

		ElkPadding pad = Options.PADDING;

		// Set the gridSize so that the entire tree + padding fits on the screen
		int gridSize = (int) Math.min(
				(target.getWidth() - pad.getHorizontal()) * 0.9
						/ (graph.nodes.stream().map(x -> x.x).max(Double::compare).get()),
				(target.getHeight() - pad.getVertical()) * 0.9
						/ (graph.nodes.stream().map(x -> x.y).max(Double::compare).get()));

		// Fix graph - make all coords positive, set size depending on new gridSize
		int minX = graph.nodes.stream().map(x -> x.x).min(Double::compare).get().intValue(),
			minY = graph.nodes.stream().map(x -> x.y).min(Double::compare).get().intValue();
		for (Node n : graph.nodes) {
			n.w = gridSize * 0.9;
			n.h = gridSize * 0.9;
			if (minX < 0)
				n.x -= minX;
			if (minY < 0)
				n.y -= minY;
		}

		// Draw the edges
		for (Edge e : graph.edges) {
			Node src = e.sources.get(0);
			Node tar = e.targets.get(0);

			drawEdge(g, target, frame, src, tar, gridSize, pad, Color.BLACK, false, false);
		}

		// Draw the nodes in the correct colours
		for (Node n : graph.nodes) {
			boolean isContour = contourNodes != null
					&& contourNodes.stream().filter(x -> x.getIdentifier().contentEquals(n.name)).findAny().isPresent();

			// draw thread
			if (!Options.hideThreads)
				if (n.thread != null) {
					Node tar = graph.nodes.stream().filter(x -> x.name.contentEquals(n.thread.getIdentifier()))
							.findFirst().get();
					drawEdge(g, target, frame, n, tar, gridSize, pad, Color.BLACK, true, true);
				}

			drawNode(g, target, frame, n, (int) n.x * gridSize + (int) pad.left, (int) n.y * gridSize + (int) pad.top,
					isContour, markedNode != null && n.name.contentEquals(markedNode.getIdentifier()));
		}

		// Draw the contour difference arrows
		g.setColor(Color.BLACK);
		if (leftArrowNode != null && rightArrowNode != null) {
			g.drawLine(
					(int) (leftArrowNode.getX() * gridSize + (int) pad.left + leftArrowNode.getWidth()
							+ 6),
					(int) (leftArrowNode.getY() * gridSize + (int) pad.top + leftArrowNode.getHeight() / 2),
					(int) (rightArrowNode.getX() * gridSize + (int) pad.left - 6),
					(int) (rightArrowNode.getY() * gridSize + (int) pad.top + rightArrowNode.getHeight() / 2));
			if (gridSize > 50) {
				g.setFont(new Font(frame.getStateLabel().getFont().getName(), Font.PLAIN, (int) (gridSize * 0.3)));
				
				// Clear background
				int size = 10;
				g.setColor(target.getBackground());
				g.fillRect( (int) (leftArrowNode.getX() * gridSize + (int) pad.left + leftArrowNode.getWidth() + 18) - size,
							(int) (leftArrowNode.getY() * gridSize + (int) pad.top + leftArrowNode.getHeight() / 2) + 1, 
							g.getFontMetrics().charWidth('4') + size * 2, g.getFontMetrics().getHeight() + size / 2);
				g.fillRect( (int) (rightArrowNode.getX() * gridSize + (int) pad.left - g.getFontMetrics().charWidth('4') - 10) - size,
							(int) (rightArrowNode.getY() * gridSize + (int) pad.top + rightArrowNode.getHeight() / 2) + 1, 
							g.getFontMetrics().charWidth('4') + size * 2, g.getFontMetrics().getHeight() + size / 2);
				g.setColor(Color.BLACK);
				
				// Draw dv string
				Node markedN = graph.nodes.stream().filter(x -> x.name.contentEquals(markedNode.getIdentifier())).findFirst().get();;
				g.drawString(Integer.toString(dv), 
						(int) (markedN.x * gridSize + (int) pad.left + 
								(markedN.w - g.getFontMetrics().stringWidth(Integer.toString(dv))) / 2),
						(int) (markedN.y * gridSize + (int) pad.top + 
								(int) (markedN.h) + g.getFontMetrics().getHeight() * 0.7));
				
				// Draw differences string
				g.drawString(Integer.toString(leftArrowNumber),
						(int) (leftArrowNode.getX() * gridSize + (int) pad.left + leftArrowNode.getWidth()
								+ 12),
						(int) (leftArrowNode.getY() * gridSize + (int) pad.top + 
								g.getFontMetrics().getHeight() + leftArrowNode.getHeight() / 2 - 5));
				g.drawString(Integer.toString(rightArrowNumber),
						(int) (rightArrowNode.getX() * gridSize + (int) pad.left - 10 - 
								g.getFontMetrics().stringWidth(Integer.toString(rightArrowNumber))),
						(int) (leftArrowNode.getY() * gridSize + (int) pad.top + 
								g.getFontMetrics().getHeight() + leftArrowNode.getHeight() / 2 - 5));
			}
		}
	}

	public static void drawNode(Graphics g, Component target, MainFrame frame, Node n, int x, int y, boolean isContour,
			boolean isMarked) {
		g.setColor(Color.BLACK);
		g.drawRect(x, y, (int) n.w, (int) n.h);

		// Set colour
		if (isMarked)
			g.setColor(Color.ORANGE);
		else if (isContour)
			g.setColor(Color.DARK_GRAY);
		else
			g.setColor(new Color(79, 195, 247));
		g.fillRect(x, y, (int) n.w, (int) n.h);

		if (isContour)
			g.setColor(Color.WHITE);
		else
			g.setColor(Color.BLACK);

		// Draw text, only draw the note if there is enough space
		if (n.h > 30 && !Options.hideNodeOffsetValues) {
			g.setFont(new Font(frame.getStateLabel().getFont().getName(), Font.PLAIN, (int) (n.h * 0.4)));

			String name = new String(n.name);
			String note = new String(n.note);
			while (g.getFontMetrics().stringWidth(name) > n.w)
				name = name.substring(0, name.length() - 2);
			while (g.getFontMetrics().stringWidth(note) > n.w)
				note = note.substring(0, note.length() - 2);

			g.drawString(name, (int) (x + (n.w - g.getFontMetrics().stringWidth(name)) / 2),
					y + g.getFontMetrics().getAscent() - (int) (n.h * 0.08));
			g.drawString(note, (int) (x + (n.w - g.getFontMetrics().stringWidth(note)) / 2),
					y + g.getFontMetrics().getHeight() * 3 / 2 + (int) (n.h * 0.1));
		} else {
			g.setFont(new Font(frame.getStateLabel().getFont().getName(), Font.PLAIN, (int) (n.h * 0.9)));

			String name = new String(n.name);
			while (g.getFontMetrics().stringWidth(name) > n.w)
				name = name.substring(0, name.length() - 2);

			g.drawString(name, (int) (x + (n.w - g.getFontMetrics().stringWidth(name)) / 2),
					(int) (y + g.getFontMetrics().getAscent() * 1.1 - 5));
		}
	}

	public static void drawEdge(Graphics g, Component target, MainFrame frame, Node src, Node tar, int gridSize,
			ElkPadding pad, Color c, boolean alwaysBottom, boolean dashed) {
		int srcX, srcY, tarX, tarY;
		if (src.x < tar.x) {
			srcX = (int) (src.x * gridSize + src.w) + (int) pad.left;
			tarX = (int) tar.x * gridSize + (int) pad.left;
		} else {
			srcX = (int) src.x * gridSize + (int) pad.left;
			tarX = (int) (tar.x * gridSize + tar.w) + (int) pad.left;
		}

		if (alwaysBottom) {
			srcY = (int) (src.y * gridSize + src.h) + (int) pad.top;
			tarY = (int) (tar.y * gridSize + tar.h) + (int) pad.top;
		} else {
			if (src.y < tar.y) {
				srcY = (int) (src.y * gridSize + src.h) + (int) pad.top;
				tarY = (int) tar.y * gridSize + (int) pad.top;
			} else {
				srcY = (int) src.y * gridSize + (int) pad.top;
				tarY = (int) (tar.y * gridSize + tar.h) + (int) pad.top;
			}
		}

		g.setColor(c);
		if (dashed)
			drawDashedLine(g, srcX, srcY, tarX, tarY);
		else 
			drawLine(g, srcX, srcY, tarX, tarY);
	}

	public static void drawDashedLine(Graphics g, int srcX, int srcY, int tarX, int tarY) {
		Graphics2D g2d = (Graphics2D) g.create();

		Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 9 }, 0);
		g2d.setStroke(dashed);
		g2d.drawLine(srcX, srcY, tarX, tarY);

		g2d.dispose();
	}
	
	public static void drawLine(Graphics g, int srcX, int srcY, int tarX, int tarY) {
		Graphics2D g2d = (Graphics2D) g.create();

		Stroke dashed = new BasicStroke(3);
		g2d.setStroke(dashed);
		g2d.drawLine(srcX, srcY, tarX, tarY);

		g2d.dispose();
	}
}

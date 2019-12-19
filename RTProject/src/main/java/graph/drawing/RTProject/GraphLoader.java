package graph.drawing.RTProject;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import phases.Phase;
import phases.RTLayoutPhase;

public class GraphLoader {
	static ElkNode curGraph = null;

	public static void load(String path, JFrame frame, Component target) {
		curGraph = parseTextFile(path, frame);

		// Add graph sizes
		curGraph.setWidth(target.getWidth());
		curGraph.setHeight(target.getHeight());
		for (ElkNode n : curGraph.getChildren())
			if (n.getWidth() == 0 || n.getHeight() == 0) {
				n.setWidth(40);
				n.setHeight(40);
			}

		applyPhase(curGraph, new RTLayoutPhase());

		drawGraph(curGraph, target.getGraphics());
	}

	private static void drawGraph(ElkNode graph, Graphics g) {
		for (ElkEdge e : curGraph.getContainedEdges()) {
			ElkConnectableShape src = e.getSources().get(0);
			ElkConnectableShape tar = e.getTargets().get(0);
			g.drawLine((int) src.getX() + (int) src.getWidth() / 2, (int) src.getY() + (int) src.getHeight() / 2,
					(int) tar.getX() + (int) tar.getWidth() / 2, (int) tar.getY() + (int) tar.getHeight() / 2);
		}

		for (ElkNode n : curGraph.getChildren()) {
			g.drawRect((int) n.getX(), (int) n.getY(), (int) n.getWidth(), (int) n.getHeight());
			g.setColor(Color.CYAN);
			g.fillRect((int) n.getX(), (int) n.getY(), (int) n.getWidth(), (int) n.getHeight());
			g.setColor(Color.BLACK);
			g.drawString(n.getIdentifier(),
					(int) (n.getX() + (n.getWidth() - g.getFontMetrics().stringWidth(n.getIdentifier())) / 2),
					(int) (n.getY() + g.getFontMetrics().getHeight()));
		}
	}

	private static void applyPhase(ElkNode graph, Phase p) {
		BasicProgressMonitor monitor = new BasicProgressMonitor();
		try {
			p.apply(curGraph, monitor);
		} catch (Exception e) {
			System.out.println("------------ LAYOUT ERROR! ------------");
			e.printStackTrace();
		}
	}

	private static ElkNode parseTextFile(String path, JFrame frame) {
		String file = readTextfile(path);
		if (file.equals("")) {
			JOptionPane.showMessageDialog(frame, "I can't read that file :/");
			return null;
		}

		String[] lines = file.split("\n");
		List<String> nodeNames = Arrays.stream(lines)
				.filter(x -> !x.contains("->") && x.trim().length() > 0 && !x.contains(":")).map(x -> {
					if (x.startsWith("node "))
						x = x.substring("node ".length());
					return x.trim();
				}).collect(Collectors.toList());
		List<List<String>> edgeNames = Arrays.stream(lines).filter(x -> x.contains("->")).map(x -> {
			if (x.startsWith("edge "))
				x = x.substring("edge ".length());
			return Arrays.stream(x.split(" -> ")).map(y -> y.trim()).collect(Collectors.toList());
		}).collect(Collectors.toList());

		ElkNode graph = ElkGraphUtil.createGraph();
		for (String n : nodeNames) {
			ElkNode node = ElkGraphUtil.createNode(graph);
			node.setIdentifier(n);
			graph.getChildren().add(node);
		}
		for (List<String> e : edgeNames) {
			ElkEdge edge = ElkGraphUtil.createEdge(graph);
			edge.getSources().add(
					graph.getChildren().stream().filter(x -> x.getIdentifier().equals(e.get(0))).findFirst().get());
			edge.getTargets().add(
					graph.getChildren().stream().filter(x -> x.getIdentifier().equals(e.get(1))).findFirst().get());
			graph.getContainedEdges().add(edge);
		}

		return graph;
	}

	private static String readTextfile(String path) {
		String content = "";

		try {
			content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}
}

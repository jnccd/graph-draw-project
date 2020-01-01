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
import javax.swing.JPanel;

import org.eclipse.elk.core.RecursiveGraphLayoutEngine;
import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkConnectableShape;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import helper.Graph;
import phases.InorderLayoutPhase;
import phases.Phase;
import phases.RTLayoutPhase;

public class GraphLoader {
	static ElkNode curGraph = null;
	
	public static ElkNode getGraph() {
		return curGraph;
	}

	public static void load(String path, MainFrame frame, JPanel target) {
		frame.states.clearStates();
		
		curGraph = parseTextFile(path, frame);

		// Add graph sizes
		curGraph.setWidth(target.getWidth());
		curGraph.setHeight(target.getHeight());
		for (ElkNode n : curGraph.getChildren())
			if (n.getWidth() == 0 || n.getHeight() == 0) {
				n.setWidth(40);
				n.setHeight(40);
			}

		applyPhase(curGraph, new InorderLayoutPhase());
		
		frame.states.addState(new GraphState("Initial State", Graph.fromElk(curGraph)));
		
		applyPhase(curGraph, new RTLayoutPhase(frame.states));
	}

	private static BasicProgressMonitor applyPhase(ElkNode graph, Phase p) {
		BasicProgressMonitor monitor = new BasicProgressMonitor();
		try {
			p.apply(curGraph, monitor);
		} catch (Exception e) {
			System.out.println("------------ LAYOUT ERROR! ------------");
			e.printStackTrace();
		}
		return monitor;
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

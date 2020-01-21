package graph.drawing.RTProject;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.eclipse.elk.core.util.BasicProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

import helper.Graph;
import phases.BinaryTreeCheckPhase;
import phases.InorderLayoutPhase;
import phases.Phase;
import phases.RTLayoutPhase;

/**
 * This class contains the necessary methods to read, parse, check and update the GraphStates in the GraphStatesManager.
 * @author dobiko
 *
 */
public class GraphLoader {
	static ElkNode curGraph = null;
	static String fileContent;

	public static ElkNode getGraph() {
		return curGraph;
	}
	
	public static void loadFile(String path, MainFrame frame, JPanel target) {
		fileContent = readTextfile(path);
		if (fileContent.equals("")) {
			JOptionPane.showMessageDialog(frame, "I can't read that file :/");
			return;
		}
		
		frame.getEditorPane().setText(fileContent);
		
		load(fileContent, frame, target);
	}

	public static boolean load(String graph, MainFrame frame, JPanel target) {
		try {
			curGraph = parseText(graph);
		} catch (Exception e) {
			return false;
		}

		// test if graph is binary tree
		try {
			new BinaryTreeCheckPhase().apply(curGraph, new BasicProgressMonitor());
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}

		frame.states.clearStates();

		// Add graph sizes
		curGraph.setWidth(target.getWidth());
		curGraph.setHeight(target.getHeight());
		
		// Layouting
		applyPhase(curGraph, new InorderLayoutPhase());

		frame.states.addState(new GraphState("Initial State", Graph.fromElk(curGraph)));

		applyPhase(curGraph, new RTLayoutPhase(frame.states));
		return true;
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

	private static ElkNode parseText(String textGraph) {
		String[] lines = textGraph.split("\n");
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

			Optional<ElkNode> snode = graph.getChildren().stream().filter(x -> x.getIdentifier().equals(e.get(0)))
					.findFirst();
			if (snode.isPresent())
				edge.getSources().add(snode.get());
			else {
				ElkNode node = ElkGraphUtil.createNode(graph);
				node.setIdentifier(e.get(0));
				graph.getChildren().add(node);

				edge.getSources().add(node);
			}

			snode = graph.getChildren().stream().filter(x -> x.getIdentifier().equals(e.get(1))).findFirst();
			if (snode.isPresent())
				edge.getTargets().add(snode.get());
			else {
				ElkNode node = ElkGraphUtil.createNode(graph);
				node.setIdentifier(e.get(1));
				graph.getChildren().add(node);

				edge.getTargets().add(node);
			}

			graph.getContainedEdges().add(edge);
		}

		return graph;
	}

	public static String readTextfile(String path) {
		String content = "";

		try {
			content = new String(Files.readAllBytes(Paths.get(path)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return content;
	}
	
	public static boolean saveTextfile(String path, String content) {
		FileWriter f = null;
		try {
			f = new FileWriter(path);
			f.write(content);
		} catch (Exception e) {
			return false;
		} finally {
			try {
				f.close();
			} catch (Exception e) { }
		}
		return true;
	}
}

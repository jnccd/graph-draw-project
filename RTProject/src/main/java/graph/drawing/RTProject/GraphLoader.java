package graph.drawing.RTProject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

public class GraphLoader {
	static ElkNode curGraph = null;
	
	public static void load(String path, JFrame frame) {
		curGraph = parseTextFile(path, frame);
		
		
	}
	
	
	private static ElkNode parseTextFile(String path, JFrame frame) {
		String file = readTextfile(path);
		if (file.equals("")) {
			JOptionPane.showMessageDialog(frame,
				    "I can't read that file :/");
			return null;
		}
		
		String[] lines = file.split("\n");
		List<String> nodeNames = Arrays.stream(lines).
				filter(x -> !x.contains("->")).
				map(x -> {
					if (x.startsWith("node "))
						x = x.substring("node ".length());
					return x;
				}).
				collect(Collectors.toList());
		List<List<String>> edgeNames = Arrays.stream(lines).
				filter(x -> x.contains("->")).
				map(x -> {
					if (x.startsWith("edge "))
						x = x.substring("edge ".length());
					return Arrays.stream(x.split(" -> ")).
						map(y -> y.trim()).
						collect(Collectors.toList());
				}).
				collect(Collectors.toList());
		
		ElkNode graph = ElkGraphUtil.createGraph();
		for (String n : nodeNames) {
			ElkNode node = ElkGraphUtil.createNode(graph);
			node.setIdentifier(n);
			graph.getChildren().add(node);
		}
		for (List<String> e : edgeNames) {
			ElkEdge edge = ElkGraphUtil.createEdge(graph);
			edge.getSources().add(graph.getChildren().stream().
					filter(x -> x.getIdentifier().equals(e.get(0))).
					findFirst().get());
			edge.getTargets().add(graph.getChildren().stream().
					filter(x -> x.getIdentifier().equals(e.get(1))).
					findFirst().get());
			graph.getContainedEdges().add(edge);
		}
		
		return graph;
	}
	
	private static String readTextfile(String path) 
    {
        String content = "";
 
        try
        {
            content = new String ( Files.readAllBytes( Paths.get(path) ) );
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
 
        return content;
    }
}

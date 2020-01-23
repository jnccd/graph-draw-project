package helper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkNode;

/**
 * This class is used to clone an Elk Graph using the fromElk() method so a GraphState can get a copy of the graph.
 * @author dobiko
 */
public class Graph {
    public List<Node> nodes = new ArrayList<Node>();
    public List<Edge> edges = new ArrayList<Edge>();
    
    /**
     * Creates a graph that is identical to the Elk graph, effectively cloning it
     * This is necessary because ElkNode sadly doesn't implement Cloneable
     * @param graph
     * @return a graph that is identical to the Elk graph
     */
    public static Graph fromElk(ElkNode graph) {
        Graph g = new Graph();
        
        for (ElkNode n : graph.getChildren())
            g.nodes.add(new Node(n.getX(), n.getY(), n.getWidth(), n.getHeight(), n.getIdentifier(), 
            		Integer.toString(Help.getProp(n).xOffset), new ArrayList<Edge>(), new ArrayList<Edge>(), n, Help.getProp(n).thread));
        
        for (ElkEdge e : graph.getContainedEdges())
            g.edges.add(new Edge(
                    g.nodes.stream().filter(x -> e.getSources().contains(x.parent)).
                    collect(Collectors.toList()), 
                    g.nodes.stream().filter(x -> e.getTargets().contains(x.parent)).
                    collect(Collectors.toList()), e));
        
        for (Node n : g.nodes) {
            n.incoming = g.edges.stream().filter(x -> x.targets.contains(n)).
                    collect(Collectors.toList());
            n.outgoing = g.edges.stream().filter(x -> x.sources.contains(n)).
                    collect(Collectors.toList());
        }
        
        return g;
    }
}

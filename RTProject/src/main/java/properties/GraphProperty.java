package properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

import helper.LongEdge;

public class GraphProperty {
    public List<List<ElkNode>> layers = new ArrayList<List<ElkNode>>();
    public List<LongEdge> longEdges = new ArrayList<LongEdge>();
}

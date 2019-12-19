package helper;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkEdge;
import org.eclipse.elk.graph.ElkEdgeSection;
import org.eclipse.elk.graph.ElkNode;
import org.eclipse.elk.graph.util.ElkGraphUtil;

public class EdgeRoutingProvider {
    
    public static void straightEdgeRouting(ElkNode layoutGraph, IElkProgressMonitor edgeRoutingMonitor) {
        if (!layoutGraph.getContainedEdges().isEmpty()) {
            for (ElkEdge edge : layoutGraph.getContainedEdges()) {
                ElkNode source = ElkGraphUtil.connectableShapeToNode(edge.getSources().get(0));
                ElkNode target = ElkGraphUtil.connectableShapeToNode(edge.getTargets().get(0));
                
                ElkEdgeSection section = ElkGraphUtil.firstEdgeSection(edge, true, true);
                
                section.setStartLocation(
                        source.getX() + source.getWidth() / 2,
                        source.getY() + source.getHeight() / 2);
                section.setEndLocation(
                        target.getX() + source.getWidth() / 2,
                        target.getY() + target.getHeight() / 2);
            }
        }
    }
}

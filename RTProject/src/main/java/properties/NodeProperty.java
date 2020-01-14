package properties;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

public class NodeProperty {
    public boolean isDummy = false;
    
    // cycleBreaking
    public boolean visiting = false;
    public boolean visited = false;
    
    // RT
    public int xOffset = 0;
    public ElkNode rightThread, leftThread;
}
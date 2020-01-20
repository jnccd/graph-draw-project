package helper;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.elk.graph.ElkNode;

/**
 * An instance of this class is saved for every ElkNode and contains needed additional information for the BinaryCheckPhase and the RTLayoutPhase.
 * @author dobiko
 *
 */
public class NodeProperty {
	// Check
    public boolean visiting = false;
    public boolean visited = false;
    
    // RT
    public int xOffset = 0;
    public ElkNode thread;
}
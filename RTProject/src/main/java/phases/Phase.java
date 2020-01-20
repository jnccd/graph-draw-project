package phases;

import org.eclipse.elk.core.util.IElkProgressMonitor;
import org.eclipse.elk.graph.ElkNode;

/**
 * This is the superclass for all phase classes in this package. It defines an apply method that gets a reference to an ElkNode that contains a graph and a ProgressMonitor.
 * @author dobiko
 *
 */
public interface Phase {
    void apply(ElkNode layoutGraph, IElkProgressMonitor monitor) throws Exception;
}

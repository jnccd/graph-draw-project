package graph.drawing.RTProject;

import org.eclipse.elk.core.math.ElkPadding;

/**
 * This class contains final and non final options for the program. The non final ones can be changed in the options tab in the GUI.
 * @author dobiko
 *
 */
public class Options {
	public static final int SPACING_NODE_NODE = 25;
	public static final ElkPadding PADDING = new ElkPadding(10, 10, 10, 10);
	
	public static int animationFrameInterval = 60;
	public static boolean hideContourStates = false;
	public static boolean hideContourDifferenceStates = false;
	public static boolean hideNodeOffsetValues = false;
	public static boolean hideThreads = false;
}

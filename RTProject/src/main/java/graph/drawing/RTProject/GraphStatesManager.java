package graph.drawing.RTProject;

import java.util.ArrayList;

public class GraphStatesManager {
	private int statesIndex = 0;
	private ArrayList<GraphState> states = new ArrayList<GraphState>();
	
	public int size() {
		return states.size();
	}
	
	public GraphState getCurrentState() {
		if (size() != 0) 
			return states.get(statesIndex);
		else
			return null;
	}
	
	public int getCurrentStateIndex() {
		return statesIndex;
	}
	
	public boolean isLastState() {
		return statesIndex == states.size() - 1;
	}
	
	public void forwardStep() {
		if (size() != 0) {
			statesIndex = (statesIndex + 1) % size();
			System.out.println("Current step " + statesIndex);
		}
	}
	
	public void backwardStep() {
		if (size() != 0) {
			statesIndex = statesIndex == 0 ? size() - 1 : statesIndex - 1;
			System.out.println("Current step " + statesIndex);
		}
	}
	
	public void clearStates() {
		statesIndex = 0;
		states.clear();
	}

	public void addState(GraphState gs) {
		states.add(gs);
		System.out.println("State added " + size());
	}
}

package edu.umass.cs.jfoley.ai;

import edu.umass.jfoley.ai.Action;
import edu.umass.jfoley.ai.State;

import java.util.List;

public abstract class SearchProblem {
  public abstract State start();
  public abstract boolean isGoal(State state);
  public abstract List<Action> actions(State from);
  public abstract double heuristic(State state);
  public abstract State getNextState(State from, Action step);
  public SearchNode childNode(SearchNode current, Action step) {
    return new SearchNode(this, current, getNextState(current.state, step), step, current.pathCost() + step.cost());
  }
  public SearchNode startNode() {
    return new SearchNode(this, null, start(), null, 0.0);
  }
}

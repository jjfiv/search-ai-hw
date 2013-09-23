package edu.umass.cs.jfoley.ai;

import java.util.List;

public abstract class SearchProblem {
  public abstract State start();
  public abstract boolean isGoal(State state);
  public abstract List<Action> actions(State from);
  public abstract double heuristic(State state);
  public abstract State getNextState(State from, Action step);
  public SearchNode childNode(SearchNode current, Action step) {
    State nextState = getNextState(current.state, step);
    double totalCost = current.cost + step.cost;

    return new SearchNode(this, current, nextState, step, totalCost);
  }
  public SearchNode startNode() {
    return new SearchNode(this, null, start(), null, 0.0);
  }
}

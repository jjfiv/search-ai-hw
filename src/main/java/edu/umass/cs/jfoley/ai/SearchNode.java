package edu.umass.cs.jfoley.ai;

import edu.umass.jfoley.ai.*;

public class SearchNode {
  public final SearchProblem problem;
  public final State state;
  public final Action action;
  public final SearchNode parent;
  public final double cost;

  private boolean cachedHeuristic = false;
  private double cachedHeuristicValue = 0;

  public SearchNode(SearchProblem problem, SearchNode parent, State state, Action action, double cost) {
    this.problem = problem;
    this.parent = parent;
    this.state = state;
    this.action = action;
    this.cost = cost;
  }

  public double heuristicCost() {
    if(!cachedHeuristic) {
      cachedHeuristicValue = problem.heuristic(state);
    }
    return cachedHeuristicValue;
  }

  public double pathCost() {
    return cost;
  }

  public double astarCost() {
    return cost + heuristicCost();
  }

  public boolean isGoal() {
    return problem.isGoal(state);
  }
}
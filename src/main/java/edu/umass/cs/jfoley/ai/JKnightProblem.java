package edu.umass.cs.jfoley.ai;


import edu.umass.jfoley.ai.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JKnightProblem implements Problem {
  private final KnightState goal;

  public JKnightProblem(int x, int y) {
    this.goal = new KnightState(x, y);
  }

  @Override
  public State start() {
    return new KnightState(0, 0);
  }

  @Override
  public boolean isGoal(State state) {
    KnightState here = (KnightState) state;
    return this.goal.x() == here.x() && this.goal.y() == here.y();
  }

  private static List<Action> staticActions;
  static {
    ArrayList<Action> tmp = new ArrayList<Action>();
    tmp.add(new KnightAction(1,2));
    tmp.add(new KnightAction(-1,2));
    tmp.add(new KnightAction(-1,-2));
    tmp.add(new KnightAction(1,-2));
    tmp.add(new KnightAction(2,1));
    tmp.add(new KnightAction(-2,1));
    tmp.add(new KnightAction(-2,-1));
    tmp.add(new KnightAction(2,-1));
    staticActions = Collections.unmodifiableList(tmp);
  }

  @Override
  public List<Action> actions(State from) {
    return staticActions;
  }

  @Override
  public double heuristic(State from) {
    KnightState here = (KnightState) from;
    return (Math.abs(goal.x() - here.x()) + Math.abs(goal.y() - here.y())) / 3.0;
  }

  @Override
  public State getNextState(State from, Action action) {
    KnightAction step = (KnightAction) action;
    KnightState here = (KnightState) from;
    return new KnightState(here.x() + step.dx(), here.y() + step.dy());
  }

  @Override
  public Node startNode() {
    return new Node(this, null, start(), null, 0.0);
  }

  @Override
  public Node childNode(Node current, Action step) {
    State next = getNextState(current.state(), step);
    return new Node(this, current, next, step, current.pathCost() + step.cost());
  }
}

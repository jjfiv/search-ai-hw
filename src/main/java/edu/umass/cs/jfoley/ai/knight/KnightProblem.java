package edu.umass.cs.jfoley.ai.knight;

import edu.umass.cs.jfoley.ai.Action;
import edu.umass.cs.jfoley.ai.SearchProblem;
import edu.umass.cs.jfoley.ai.State;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KnightProblem extends SearchProblem {

  private final KnightState goal;

  public KnightProblem(int x, int y) {
    this.goal = new KnightState(x, y);
  }

  @Override
  public State start() {
    return new KnightState(0, 0);
  }

  @Override
  public boolean isGoal(State state) {
    return this.goal.equals(state);
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

    double dx = goal.x - here.x;
    double dy = goal.y - here.y;
    double mdh = ((double) (Math.abs(dx) + Math.abs(dy))) / 3.0;

    //System.out.println("("+dx+","+dy+")" + " h:"+mdh);

    return mdh;
  }

  @Override
  public State getNextState(State from, Action action) {
    KnightAction step = (KnightAction) action;
    KnightState here = (KnightState) from;
    return new KnightState(here.x + step.dx, here.y + step.dy);
  }
}

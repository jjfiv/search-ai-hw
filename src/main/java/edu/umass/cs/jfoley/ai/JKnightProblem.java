package edu.umass.cs.jfoley.ai;


import edu.umass.jfoley.ai.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JKnightProblem extends SearchProblem {
  public static class KnightState implements State {
    public final int x;
    public final int y;

    public KnightState(int x, int y) {
      this.x = x;
      this.y = y;
    }

    @Override
    public String toString() {
      return "S("+x+","+y+")";
    }

    @Override
    public boolean equals(Object obj) {
      if(obj == this) {
        return true;
      } else if( !(obj instanceof KnightState)) {
        return false;
      } else {
        KnightState that = (KnightState) obj;
        return this.x == that.x && this.y == that.y;
      }
    }

    @Override
    public int hashCode() {
      return Utility.hash(x) ^ Utility.hash(y);
    }
  }

  public static class KnightAction implements Action {
    public final int dx;
    public final int dy;

    public KnightAction(int x, int y) {
      this.dx = x;
      this.dy = y;
    }
    @Override
    public double cost() {
      return 1.0;
    }
    @Override
    public String toString() {
      return "A<"+dx+","+dy+">";
    }
  }

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
    return (Math.abs(goal.x - here.x) + Math.abs(goal.y - here.y)) / 3.0;
  }

  @Override
  public State getNextState(State from, Action action) {
    KnightAction step = (KnightAction) action;
    KnightState here = (KnightState) from;
    return new KnightState(here.x + step.dx, here.y + step.dy);
  }
}

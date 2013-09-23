package edu.umass.cs.jfoley.ai;

public abstract class Action {
  public double cost;
  public Action(double cost) {
    this.cost = cost;
  }
}

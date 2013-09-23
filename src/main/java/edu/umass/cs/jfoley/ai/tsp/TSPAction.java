package edu.umass.cs.jfoley.ai.tsp;

import edu.umass.cs.jfoley.ai.Action;

public class TSPAction extends Action {
  public final int city;

  public TSPAction(int city, double distance) {
    super(distance);
    this.city = city;
  }
}

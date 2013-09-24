package edu.umass.cs.jfoley.ai.tsp;

public class TSPPoint {
  public double x, y;
  public TSPPoint(double x, double y) {
    this.x = x;
    this.y = y;
  }

  @Override
  public String toString() {
    return String.format("(%1.2f,%1.2f)", x, y);
  }
}

package edu.umass.cs.jfoley.ai.knight;

import edu.umass.cs.jfoley.ai.Action;

public class KnightAction extends Action {
  public final int dx;
  public final int dy;

  public KnightAction(int x, int y) {
    super(1.0);
    this.dx = x;
    this.dy = y;
  }

  @Override
  public String toString() {
    return "A<"+dx+","+dy+">";
  }
}

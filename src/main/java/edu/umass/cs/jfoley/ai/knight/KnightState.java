package edu.umass.cs.jfoley.ai.knight;

import edu.umass.cs.jfoley.ai.State;
import edu.umass.cs.jfoley.ai.Utility;

public class KnightState extends State {
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
    return (Utility.hash(x) * 17) ^ Utility.hash(y);
  }
}

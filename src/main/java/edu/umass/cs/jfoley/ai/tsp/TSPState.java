package edu.umass.cs.jfoley.ai.tsp;

import edu.umass.cs.jfoley.ai.State;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;

public class TSPState extends State {
  public TSPState(double distance, TIntArrayList route, TIntHashSet remaining) {

  }

  public int currentCity() {
    return 0;
  }

  @Override
  public int hashCode() {
    return 0;  //To change body of implemented methods use File | Settings | File Templates.
  }

  @Override
  public boolean equals(Object obj) {
    return false;  //To change body of implemented methods use File | Settings | File Templates.
  }
}

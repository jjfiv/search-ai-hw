package edu.umass.cs.jfoley.ai.tsp;

import edu.umass.cs.jfoley.ai.State;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;

public class TSPState extends State {
  public final TIntHashSet remaining;
  public final double distance;
  public final TIntArrayList route;
  public int currentCity;
  public int firstCity;
  public int size;

  public TSPState(double distance, TIntArrayList route, TIntHashSet remaining) {
    this.distance = distance;
    this.route = route;

    this.size = route.size();
    this.currentCity = -1;
    this.firstCity = -1;
    if(size > 0) {
      this.currentCity = route.get(size-1);
      this.firstCity = route.get(0);
    }

    this.remaining = remaining;
  }

  private TIntHashSet _visited = null;
  public TIntHashSet visited() {
    if(_visited == null) {
      _visited = new TIntHashSet();
      _visited.addAll(this.route);
    }

    return _visited;
  }

  public String toString() {
    return "TSPState: "+route.toString();
  }

  @Override
  public int hashCode() {
    return route.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if(obj == this) {
      return true;
    } else if( !(obj instanceof TSPState)) {
      return false;
    } else {
      TSPState that = (TSPState) obj;

      int left = this.route.size();
      int right = that.route.size();

      if(left != right) {
        return false;
      }

      for(int i=0; i<left; i++) {
        if(this.route.getQuick(i) != that.route.getQuick(i)) {
          return false;
        }
      }

      return true;
    }
  }
}

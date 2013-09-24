package edu.umass.cs.jfoley.ai.tsp;

import edu.umass.cs.jfoley.ai.Utility;

import java.util.Comparator;
import java.util.PriorityQueue;

public class MSTLength {
  private static class MSTEdge {
    public final double weight;
    public final int a, b;

    public final TSPPoint pa, pb;

    public MSTEdge(TSPPoint[] points, int[] indices, int a, int b) {
      this.a = a;
      this.b = b;
      this.pa = points[indices[a]];
      this.pb = points[indices[b]];
      this.weight = dist(pa, pb);
    }

    @Override
    public String toString() {
      return pa + " - " + pb + " w:" + weight;
    }
  }

  private static double dist(TSPPoint a, TSPPoint b) {
    double dx = a.x - b.x;
    double dy = a.y - b.y;
    return Math.sqrt(dx*dx + dy*dy);
  }

  public static double calculate(int[] indices, TSPPoint[] points) {
    if(indices.length == 0) {
      return 0;
    }

    // sort edges by weight:
    PriorityQueue<MSTEdge> edges = new PriorityQueue<MSTEdge>(100, new Comparator<MSTEdge>() {
      @Override
      public int compare(MSTEdge mstEdge, MSTEdge mstEdge2) {
        return Utility.compare(mstEdge.weight, mstEdge2.weight);
      }
    });

    // generate edges
    for(int i=0; i<indices.length; i++) {
      for(int j=i+1; j<indices.length; j++) {
        edges.offer(new MSTEdge(points, indices, i, j));
      }
    }

    int[] sets = new int[indices.length];
    for(int i=0; i<indices.length; i++) {
      sets[i] = i;
    }

    // calculate length of minimum spanning tree by grouping the smallest edges until they're all in the same group
    double h = 0;
    while(!edges.isEmpty()) {
      MSTEdge edge = edges.poll();
      if(sets[edge.a] != sets[edge.b]) {
        // merge set a and b
        int pullIn = sets[edge.b];
        for(int i=0; i<sets.length; i++) {
          if(sets[i] == pullIn) {
            sets[i] = sets[edge.a];
          }
        }
        h += edge.weight;
      }
    }

    return h;
  }

  public static void main(String[] args) {
    assert(3.0 == calculate(new int[] {0,1,2,3}, new TSPPoint[] {
      new TSPPoint(0,0),
      new TSPPoint(1,0),
      new TSPPoint(2,0),
      new TSPPoint(3,0)
    }));

    assert(3.0 == calculate(new int[] {0,1,2,3}, new TSPPoint[] {
      new TSPPoint(0,0),
      new TSPPoint(1,0),
      new TSPPoint(2,0),
      new TSPPoint(2,1)
    }));
  }
}

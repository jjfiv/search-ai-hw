package edu.umass.cs.jfoley.ai.knight;

import edu.umass.cs.jfoley.ai.AStar;
import edu.umass.cs.jfoley.ai.SearchResult;

import java.util.Random;

public class KnightPlotter {
  public static void main(String[] args) {
    int numPoints = 10000;

    Random rand = new Random();

    for(int i=0; i<numPoints; i++) {
      int x = rand.nextInt() % 60;
      int y = rand.nextInt() % 60;
      KnightProblem problem = new KnightProblem(x, y);

      long start = System.currentTimeMillis();
      SearchResult sr = AStar.search(problem);
      long end = System.currentTimeMillis();

      double time = (end - start) / 1000.0;
      System.out.println(String.format("%1.4f\t%d\t%1.4f", sr.result.cost, sr.expandedNodes, time));
    }
  }
}


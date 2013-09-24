package edu.umass.cs.jfoley.ai.tsp;

import edu.umass.cs.jfoley.ai.AStar;
import edu.umass.cs.jfoley.ai.SearchResult;

import java.util.Random;

public class TSPPlotter {
  public static void main(String[] args) {
    int numPoints = 10000;

    Random rand = new Random();

    for(int i=0; i<numPoints; i++) {
      int numCities = 3+Math.abs(rand.nextInt() % 12);
      TSPPoint[] cities = EuclideanTSP.random(numCities);
      EuclideanTSP problem = new EuclideanTSP(cities);

      long start = System.currentTimeMillis();
      SearchResult sr = AStar.search(problem);
      long end = System.currentTimeMillis();

      double time = (end - start) / 1000.0;
      System.out.println(String.format("%d\t%d\t%1.4f", numCities, sr.expandedNodes, time));
    }
  }
}


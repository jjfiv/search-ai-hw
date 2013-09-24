package edu.umass.cs.jfoley.ai.tsp;

import edu.umass.cs.jfoley.ai.*;
import gnu.trove.list.array.TIntArrayList;
import gnu.trove.set.hash.TIntHashSet;

import java.util.*;

public class EuclideanTSP extends SearchProblem {
  private final TSPPoint[] cities;
  private final int numCities;

  public EuclideanTSP(TSPPoint[] cities) {
    this.cities = cities;
    this.numCities = cities.length;
  }

  @Override
  public State start() {
    TIntHashSet remaining = new TIntHashSet();
    for(int i=1; i<numCities; i++) {
      remaining.add(i);
    }
    TIntArrayList visited = new TIntArrayList();
    visited.add(0);
    return new TSPState(0.0, visited, remaining);
  }

  @Override
  public boolean isGoal(State state) {
    TSPState trip = (TSPState) state;
    return trip.size == numCities + 1 && trip.currentCity == trip.firstCity;
  }

  private double distanceBetween(int i, int j) {
    TSPPoint ci = cities[i];
    TSPPoint cj = cities[j];
    double dx = ci.x - cj.x;
    double dy = ci.y - cj.y;
    return Math.sqrt(dx*dx + dy*dy);
  }

  @Override
  public List<Action> actions(State from) {
    TSPState trip = (TSPState) from;

    ArrayList<Action> results = new ArrayList<Action>();

    assert(trip.size != 0);
    if(trip.size < numCities) {
      int cur = trip.currentCity;
      System.out.println(trip.route);
      System.out.println(trip.remaining);
      System.out.println("TRIP_SIZE: "+trip.size);
      System.out.println("REM_SIZE: "+trip.remaining.size());

      assert(trip.size + trip.remaining.size() == numCities);
      //System.out.println("@"+cur+" A.R:"+trip.remaining);
      for(int city : trip.remaining.toArray()) {
        if(city != trip.firstCity) {
          results.add(new TSPAction(city, distanceBetween(cur, city)));
        }
      }
    } else {
      assert(trip.remaining.size() == 0);
      results.add(new TSPAction(trip.firstCity, distanceBetween(trip.firstCity, trip.currentCity)));
    }

    return results;
  }

  @Override
  public double heuristic(State state) {
    TSPState trip = (TSPState) state;

    //System.out.println("heuristic: "+trip.route+" R:"+trip.remaining);
    TIntHashSet rest = trip.remaining;
    rest.add(trip.firstCity);

    return MSTLength.calculate(rest.toArray(), cities);
  }


  private TIntHashSet remainingCities(TIntArrayList visited) {
    TIntHashSet opposite = new TIntHashSet();
    for(int id=0; id<numCities; id++) {
      if(!visited.contains(id)) {
        opposite.add(id);
      }
    }
    System.out.println("V_SIZE: "+visited.size());
    System.out.println("R_SIZE: "+opposite.size());
    assert(visited.size() + opposite.size() == numCities);
    //System.out.println("V: "+visited);
    //System.out.println("R: "+opposite);
    return opposite;
  }

  @Override
  public State getNextState(State from, Action step) {
    TSPState soFar = (TSPState) from;
    TSPAction nextTrip = (TSPAction) step;

    double total = soFar.distance + nextTrip.cost;
    TIntArrayList route = new TIntArrayList();
    route.addAll(soFar.route);
    route.add(nextTrip.city);

    TIntHashSet remaining = remainingCities(route);
    assert(route.size() + remaining.size() == numCities);

    return new TSPState(total, route, remaining);
  }




  public static EuclideanTSP random(int n) {
    Random rand = new Random(13);
    TSPPoint[] cities = new TSPPoint[n];
    for(int i=0; i<n; i++) {
      cities[i] = new TSPPoint(rand.nextDouble(), rand.nextDouble());
    }
    return new EuclideanTSP(cities);
  }

  public static void main(String[] args) {
    long start = System.currentTimeMillis();

    SearchResult box = AStar.search(new EuclideanTSP(new TSPPoint[] { new TSPPoint(0,0), new TSPPoint(1,0), new TSPPoint(1,1), new TSPPoint(1,0) }));
    System.out.println(box.result.state);
    System.out.println(box.result.cost);
    assert(box.result.cost == 5);

    SearchResult line = AStar.search(new EuclideanTSP(new TSPPoint[] { new TSPPoint(0,0), new TSPPoint(1,0), new TSPPoint(2,0), new TSPPoint(3,0), new TSPPoint(10,0), new TSPPoint(15,0), new TSPPoint(20,0) }));
    assert(line.result.cost == 40);
  }
}

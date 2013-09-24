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
    TIntArrayList visited = new TIntArrayList();
    visited.add(0);
    return new TSPState(0.0, visited, remainingCities(visited));
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

    TIntHashSet foo = remainingCities(trip.route);
    assert(foo.equals(trip.remaining));

    ArrayList<Action> results = new ArrayList<Action>();

    assert(trip.remaining.equals(remainingCities(trip.route)));

    assert(trip.size != 0);
    if(trip.size < numCities) {
      int cur = trip.currentCity;
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

    if(isGoal(trip)) {
      return 0;
    }

    //System.out.println("heuristic: "+trip.route+" R:"+trip.remaining);
    TIntHashSet rest = new TIntHashSet(trip.remaining);
    rest.add(trip.firstCity);

    assert(!trip.remaining.contains(trip.firstCity));

    return MSTLength.calculate(rest.toArray(), cities);
  }


  private TIntHashSet remainingCities(TIntArrayList visited) {
    if(visited.size() > numCities) {
      return new TIntHashSet();
    }

    TIntHashSet opposite = new TIntHashSet();
    for(int id=0; id<numCities; id++) {
      if(!visited.contains(id)) {
        opposite.add(id);
      }
    }
    assert(visited.size() + opposite.size() == numCities);
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
    return new TSPState(total, route, remaining);
  }

  public static TSPPoint[] random(int n) {
    Random rand = new Random();
    TSPPoint[] cities = new TSPPoint[n];
    for(int i=0; i<n; i++) {
      cities[i] = new TSPPoint(rand.nextDouble(), rand.nextDouble());
    }
    return cities;
  }

  public static void main(String[] args) {
    SearchResult box = AStar.search(new EuclideanTSP(new TSPPoint[] { new TSPPoint(0,0), new TSPPoint(1,0), new TSPPoint(1,1), new TSPPoint(0,1) }));
    System.out.println(box.result.state);
    System.out.println(box.result.cost);
    SearchNode n = box.result;
    while(n != null && n.action != null) {
      System.out.println(n.action);
      n = n.parent;
    }

    assert(box.result.cost == 4);

    SearchResult line = AStar.search(new EuclideanTSP(new TSPPoint[] { new TSPPoint(0,0), new TSPPoint(1,0), new TSPPoint(2,0), new TSPPoint(3,0), new TSPPoint(10,0), new TSPPoint(15,0), new TSPPoint(20,0) }));
    System.out.println(line.result.state);
    System.out.println(line.result.cost);
    assert(line.result.cost == 40);

    // proof by probabilistic guarantee
    int numTrials = 10000;

    for(int i=0; i<numTrials; i++) {
      TSPPoint[] cities = EuclideanTSP.random(7);
      SearchResult opt = AStar.bfs(new EuclideanTSP(cities));
      System.out.println(opt.result.cost);
      System.out.println(opt.result.state);

      SearchResult heur = AStar.search(new EuclideanTSP(cities));
      System.out.println(heur.result.cost);
      System.out.println(heur.result.state);
      if(heur.result.cost != opt.result.cost) {
        System.out.println("Problem on which A* != BFS:");
        for(TSPPoint city : cities) {
          System.out.println(city);
        }
      }
      assert(heur.result.cost == opt.result.cost);
    }

  }
}

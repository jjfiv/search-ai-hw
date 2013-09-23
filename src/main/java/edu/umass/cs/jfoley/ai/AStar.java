package edu.umass.cs.jfoley.ai;

import edu.umass.jfoley.ai.State;
import gnu.trove.set.hash.THashSet;

import java.util.Comparator;
import java.util.PriorityQueue;

public class AStar {

  private static class NodeComparator implements Comparator<SearchNode> {
    @Override
    public int compare(SearchNode a, SearchNode b) {
      return Utility.compare(a.astarCost(), b.astarCost());
    }
  }

  public static SearchResult search(SearchProblem prob) {
    PriorityQueue<SearchNode> frontier = new PriorityQueue<SearchNode>(10, new NodeComparator());
    frontier.offer(prob.startNode());

    THashSet<State> explored = new THashSet<State>();

    while(!frontier.isEmpty()) {
      SearchNode candidate = frontier.poll();
      explored.add(candidate.state);

      if(candidate.isGoal()) {
        return new SearchResult(candidate, explored.size(), frontier.size());
      }
    }

    return null;
  }
}

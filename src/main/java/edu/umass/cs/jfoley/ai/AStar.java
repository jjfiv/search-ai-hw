package edu.umass.cs.jfoley.ai;

import java.util.*;

public class AStar {
  public static SearchResult generic(SearchProblem prob, Comparator<SearchNode> cmp) {
    PriorityQueue<SearchNode> frontier = new PriorityQueue<SearchNode>(10000, cmp);
    frontier.offer(prob.startNode());

    Set<State> explored = new HashSet<State>();

    while(!frontier.isEmpty()) {
      SearchNode candidate = frontier.poll();

      if(candidate.isGoal()) {
        return new SearchResult(candidate, explored.size(), frontier.size());
      }

      List<Action> nextActions = prob.actions(candidate.state);
      for(Action action : nextActions) {
        SearchNode child = prob.childNode(candidate, action);
        if (!explored.contains(child.state)) {
          frontier.offer(child);
          explored.add(candidate.state);
        }
      }
    }

    return null;
  }

  public static SearchResult search(SearchProblem prob) {
    return generic(prob, new Comparator<SearchNode>() {
      @Override
      public int compare(SearchNode a, SearchNode b) {
        return Utility.compare(a.astarCost(), b.astarCost());
      }
    });
  }

  public static SearchResult bfs(SearchProblem prob) {
    return generic(prob, new Comparator<SearchNode>() {
      @Override
      public int compare(SearchNode a, SearchNode b) {
        return Utility.compare(a.cost, b.cost);
      }
    });
  }
}

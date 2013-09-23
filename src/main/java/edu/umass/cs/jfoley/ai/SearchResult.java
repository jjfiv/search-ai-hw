package edu.umass.cs.jfoley.ai;

public class SearchResult {
  public final SearchNode result;
  public final int expandedNodes;
  public final int frontierNodes;

  public SearchResult(SearchNode result, int expandedNodes, int frontierNodes) {
    this.result = result;
    this.expandedNodes = expandedNodes;
    this.frontierNodes = frontierNodes;
  }
}

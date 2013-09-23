package edu.umass.jfoley.ai

import gnu.trove.set.hash.THashSet
import scala.collection.JavaConverters._
import edu.umass.cs.jfoley.ai.{SearchProblem, SearchNode}

sealed trait SearchResult
case class FoundResult(node: SearchNode, expandedNodes: Int, frontierNodes: Int) extends SearchResult
case class NoSolution() extends SearchResult


trait Action {
  def cost(): Double
}
trait State {}

object SP {

  /** Recursively print steps to solution */
  def printSolution(goal: SearchNode) {
    if(goal.parent != null) {
      printSolution(goal.parent)
      println(goal.action+" -> "+goal.state)
    } else {
      println("START -> "+goal.state)
    }
  }

  /** Implement A* Search */
  def astar(prob: SearchProblem): SearchResult = {
    val startNode = prob.startNode()

    var frontier = new java.util.PriorityQueue[SearchNode](100, new java.util.Comparator[SearchNode] {
      def compare(a: SearchNode, b: SearchNode): Int = { (a.astarCost() - b.astarCost()).toInt }
    })
    frontier.offer(startNode)
    var explored = new THashSet[State]()

    while(!frontier.isEmpty) {
      val candidate = frontier.poll()

      // if the lowest cost thing is the goal, we're done
      if(candidate.isGoal) {
        return FoundResult(candidate, explored.size, frontier.size)
      }

      //println("A* explore: "+candidate.state+" frontier.size="+frontier.size)

      prob.actions(candidate.state).asScala.foreach(action => {
        val child = prob.childNode(candidate, action)
        if(!explored.contains(child.state)) {
          frontier.offer(child)
          explored.add(child.state)
        }
      })
    }

    NoSolution()
  }

  /** Implement BFS for debugging */
  def bfs(prob: SearchProblem): SearchResult = {
    val startNode = prob.startNode()
    if(startNode.isGoal) {
      return FoundResult(startNode, 0, 0)
    }

    var frontier = Seq(startNode)
    var explored = Set[State]()

    while(!frontier.isEmpty) {
      val candidate = frontier.head
      frontier = frontier.tail
      explored += candidate.state

      //println("BFS explore: "+candidate.state+" frontier.size="+frontier.size)

      val newNodes: Seq[SearchNode] = prob.actions(candidate.state).asScala.flatMap(action => {
        val child = prob.childNode(candidate, action)
        if(frontier.exists(_.state == child.state) || explored.contains(child.state)) {
          None
        } else {
          if(child.isGoal) {
            return FoundResult(child, explored.size, frontier.size)
          }
          Some(child)
        }
      })

      frontier = frontier ++ newNodes
    }

    NoSolution()
  }

  /** Implement Greedy for debugging */
  def greedy(prob: SearchProblem): SearchResult = {
    val startNode = prob.startNode()
    if(startNode.isGoal) {
      return FoundResult(startNode, 0, 0)
    }

    var frontier = Seq(startNode)
    var explored = Set[State]()

    while(!frontier.isEmpty) {
      val candidate = frontier.head
      frontier = frontier.tail
      explored += candidate.state

      //println("BFS explore: "+candidate.state+" frontier.size="+frontier.size)

      val newNodes: Seq[SearchNode] = prob.actions(candidate.state).asScala.flatMap(action => {
        val child = prob.childNode(candidate, action)
        if(frontier.exists(_.state == child.state) || explored.contains(child.state)) {
          None
        } else {
          if(child.isGoal) {
            return FoundResult(child, explored.size, frontier.size)
          }
          Some(child)
        }
      })

      frontier = (frontier ++ newNodes).sortBy(node => node.action.cost())
    }

    NoSolution()
  }

}
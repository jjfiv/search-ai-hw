package edu.umass.jfoley.ai

import gnu.trove.impl.hash.TObjectHash
import gnu.trove.set.hash.THashSet

sealed trait SearchResult
case class FoundResult(node: Node, expandedNodes: Int, frontierNodes: Int) extends SearchResult
case class NoSolution() extends SearchResult

final case class Node(problem: Problem, parent: Node, state: State, action: Action, cost: Double) {
  def heuristicCost() = problem.heuristic(state)
  def pathCost() = cost
  def astarCost() = heuristicCost() + cost
  def isGoal = problem.isGoal(state)
}

trait Action {
  def cost(): Double
}
trait State {}

trait Problem {
  def start: State
  def isGoal(state: State): Boolean
  def actions(from: State): Seq[Action]
  def heuristic(from: State): Double
  def getNextState(from: State, action: Action): State

  final def childNode(current: Node, step: Action): Node = {
    val nextState = getNextState(current.state, step)
    Node(this, current, nextState, step, current.pathCost() + step.cost)
  }
  final def startNode(): Node = Node(this, null, start, null, 0.0)
}


object SearchProblem {

  /** Recursively print steps to solution */
  def printSolution(goal: Node) {
    if(goal.parent != null) {
      printSolution(goal.parent)
      println(goal.action+" -> "+goal.state)
    } else {
      println("START -> "+goal.state)
    }
  }

  /** Implement A* Search */
  def astar(prob: Problem): SearchResult = {
    val startNode = prob.startNode()

    var frontier = new java.util.PriorityQueue[Node](100, new java.util.Comparator[Node] {
      def compare(a: Node, b: Node): Int = { (a.astarCost() - b.astarCost()).toInt }
    })
    frontier.offer(startNode)
    var explored = new THashSet[State]()

    while(!frontier.isEmpty) {
      val candidate = frontier.poll()
      explored.add(candidate.state)

      // if the lowest cost thing is the goal, we're done
      if(candidate.isGoal) {
        return FoundResult(candidate, explored.size, frontier.size)
      }

      //println("A* explore: "+candidate.state+" frontier.size="+frontier.size)

      prob.actions(candidate.state).foreach(action => {
        val child = prob.childNode(candidate, action)
        if(!explored.contains(child.state)) {
          frontier.offer(prob.childNode(candidate, action))
          explored.add(child.state)
        }
      })
    }

    NoSolution()
  }

  /** Implement BFS for debugging */
  def bfs(prob: Problem): SearchResult = {
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

      val newNodes: Seq[Node] = prob.actions(candidate.state).flatMap(action => {
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
  def greedy(prob: Problem): SearchResult = {
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

      val newNodes: Seq[Node] = prob.actions(candidate.state).flatMap(action => {
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
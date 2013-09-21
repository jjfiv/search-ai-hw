package edu.umass.jfoley.ai

sealed trait SearchResult
case class FoundResult(node: Node, expandedNodes: Int) extends SearchResult
case class NoSolution() extends SearchResult

final case class Node(problem: Problem, parent: Node, state: State, action: Action, cost: Double) {
  def heuristicCost() = problem.heuristic(state)
  def pathCost() = cost
  def astarCost() = heuristicCost() + cost
  def isGoal = problem.goal == state
}

trait Action {
  def cost(): Double
}
trait State {}

trait Problem {
  def start: State
  def goal: State
  def actions(from: State): Seq[Action]
  def heuristic(from: State): Double
  def getNextState(from: State, action: Action): State

  final def childNode(current: Node, step: Action): Node = {
    val nextState = getNextState(current.state, step)
    Node(this, current, nextState, step, current.pathCost() + step.cost)
  }
  final def startNode(): Node = Node(this, null, start, null, 0.0)
  final def isGoal(state: State) = goal == state
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

    var frontier = Seq(startNode)
    var explored = Set[State]()

    while(!frontier.isEmpty) {
      val candidate = frontier.head
      frontier = frontier.tail
      explored += candidate.state

      // if the lowest cost thing is the goal, we're done
      if(candidate.isGoal) {
        return FoundResult(candidate, explored.size)
      }

      //println("A* explore: "+candidate.state+" frontier.size="+frontier.size)

      val newNodes: Seq[Node] = prob.actions(candidate.state).flatMap(action => {
        val child = prob.childNode(candidate, action)
        if(frontier.exists(_.state == child.state) || explored.contains(child.state)) {
          None
        } else {
          Some(prob.childNode(candidate, action))
        }
      })

      frontier = (frontier ++ newNodes).sortBy(_.astarCost())
    }

    NoSolution()
  }

  /** Implement BFS for debugging */
  def bfs(prob: Problem): SearchResult = {
    val startNode = prob.startNode()
    if(startNode.isGoal) {
      return FoundResult(startNode, 0)
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
            return FoundResult(child, explored.size)
          }
          Some(child)
        }
      })

      frontier = frontier ++ newNodes
    }

    NoSolution()
  }

}
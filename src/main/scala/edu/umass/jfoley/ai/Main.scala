
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


object Main {
  def main(args: Array[String]) {
    println("Hello World!")

    bfs(KnightProblem(1,1)) match {
      case FoundResult(node, _) => {
        println(node)
        printSolution(node)
      }
      case NoSolution() => {
        throw new RuntimeException("Expected solution to KnightProblem(1,1)")
      }
    }

    bfs(KnightProblem(4,3)) match {
      case FoundResult(node, _) => {
        printSolution(node)
      }
    }

    astar(KnightProblem(4,3)) match {
      case FoundResult(node, _) => printSolution(node)
    }

    astar(KnightProblem(100,100)) match {
      case FoundResult(node, expNodes) => println("A* (0,0 -> 100,100) nodes: "+expNodes); printSolution(node)
    }
    bfs(KnightProblem(100,100)) match {
      case FoundResult(node, expNodes) => println("BFS (0,0 -> 100,100) nodes: "+expNodes)//printSolution(node)
    }
  }


  def printSolution(goal: Node) {
    if(goal.parent != null) {
      printSolution(goal.parent)
      println(goal.action+" -> "+goal.state)
    } else {
      println("START -> "+goal.state)
    }
  }

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
}

case class KnightState(x: Int, y: Int) extends State {
  override def toString = "S("+x+","+y+")"
}
case class KnightAction(dx: Int, dy: Int) extends Action {
  override def toString = "A<"+dx+","+dy+">"
  def cost() = 1.0
}

case class KnightProblem(goalX: Int, goalY: Int) extends Problem {
  val goal = KnightState(goalX, goalY)
  val start = KnightState(0,0)

  private val staticActions = Seq((1,2), (2,1)).flatMap {
    case (x,y) => Seq(KnightAction(x,y), KnightAction(-x,y), KnightAction(-x,-y), KnightAction(x,-y))
  }
  def actions(from: State) = staticActions

  def heuristic(from: State): Double = {
    val here = from.asInstanceOf[KnightState]
    math.floor(math.abs(goal.x - here.x) + math.abs(goal.y - here.y).toDouble / 3.0)
  }

  def getNextState(from: State, action: Action): State = {
    val step = action.asInstanceOf[KnightAction]
    val here = from.asInstanceOf[KnightState]
    KnightState(here.x + step.dx, here.y + step.dy)
  }
}
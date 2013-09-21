
sealed trait SearchResult
case class FoundResult(node: Node, expandedNodes: Int) extends SearchResult
case class NoSolution() extends SearchResult

final case class Node(problem: Problem, parent: Node, state: State, action: Action, cost: Double) {
  def heuristicCost() = problem.heuristic(state)
  def pathCost() = cost
  def astarCost() = heuristicCost() + cost
  def isGoal = problem.goal == state
}

trait Action {}
trait State {}

trait Problem {
  final def startNode(): Node = Node(this, null, start, null, 0.0)
  def start: State
  def goal: State
  def isGoal(state: State) = goal == state
  def actions: Seq[Action]
  def heuristic(from: State): Double
  def childNode(current: Node, step: Action): Node
}

trait TypedProblem[AT <: Action, ST <: State] extends Problem {
  override def childNode(current: Node, step: Action): Node = makeChildNode(current, step.asInstanceOf[AT])
  def makeChildNode(current: Node, step: AT): Node
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

  def bfs(kp: Problem): SearchResult = {
    val startNode = kp.startNode()
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

      val newNodes: Seq[Node] = kp.actions.flatMap(action => {
        val child = kp.childNode(candidate, action)
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

  def astar(kp: Problem): SearchResult = {
    val startNode = kp.startNode()

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

      val newNodes: Seq[Node] = kp.actions.flatMap(action => {
        val child = kp.childNode(candidate, action)
        if(frontier.exists(_.state == child.state) || explored.contains(child.state)) {
          None
        } else {
          Some(kp.childNode(candidate, action))
        }
      })

      //val bestNew = newNodes.minBy(_.astarCost())
      //println("A* expand "+candidate.state+" most likely: "+bestNew.state+ " h(n): "+bestNew.heuristicCost()+ " f(n): "+bestNew.pathCost)

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
}

case class KnightProblem(goalX: Int, goalY: Int) extends TypedProblem[KnightAction, KnightState] {
  val goal = KnightState(goalX, goalY)
  val start = KnightState(0,0)

  val actions: Seq[Action] = {
    Seq(
      (1,2),
      (2,1)
    ).flatMap {
      case (x,y) => Seq(KnightAction(x,y), KnightAction(-x,y), KnightAction(-x,-y), KnightAction(x,-y))
    }
  }

  def heuristic(from: State): Double = {
    val here = from.asInstanceOf[KnightState]
    math.floor(math.abs(goal.x - here.x) + math.abs(goal.y - here.y).toDouble / 3.0)
  }

  def childState(parent: KnightState, action: KnightAction): KnightState = {
    KnightState(parent.x + action.dx, parent.y + action.dy)
  }

  def makeChildNode(parent: Node, action: KnightAction): Node = {
    val nextState = childState(parent.state.asInstanceOf[KnightState], action)
    Node(this, parent, nextState, action, parent.pathCost + 1)
  }
}
package edu.umass.jfoley.ai

object Knight {
  import SearchProblem._

  def main(args: Array[String]) {
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

  def isGoal(st: State) = st == goal

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


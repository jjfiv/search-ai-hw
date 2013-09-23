package edu.umass.jfoley.ai

import edu.umass.cs.jfoley.ai.JKnightProblem

object Knight {
  import SP._

  def main(args: Array[String]) {
    bfs(new JKnightProblem(1,1)) match {
      case FoundResult(node, _, _) => {
        println(node)
        printSolution(node)
      }
      case NoSolution() => {
        throw new RuntimeException("Expected solution to KnightProblem(1,1)")
      }
    }

    bfs(new JKnightProblem(4,3)) match {
      case FoundResult(node, _, _) => {
        printSolution(node)
      }
    }

    astar(new JKnightProblem(4,3)) match {
      case FoundResult(node, _, _) => printSolution(node)
    }

    astar(new JKnightProblem(100,100)) match {
      case FoundResult(node, expNodes, _) => println("A* (0,0 -> 100,100) nodes: "+expNodes); printSolution(node)
    }
    bfs(new JKnightProblem(100,100)) match {
      case FoundResult(node, expNodes, _) => println("BFS (0,0 -> 100,100) nodes: "+expNodes)//printSolution(node)
    }
  }


}


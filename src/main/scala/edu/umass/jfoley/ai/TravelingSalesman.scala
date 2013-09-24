package edu.umass.jfoley.ai

import edu.umass.cs.jfoley.ai.AStar
import edu.umass.cs.jfoley.ai.tsp.{TSPPoint, EuclideanTSP, TSPState}

object TravelingSalesman {
  val rand = new scala.util.Random(130)

  def genCities(n: Int): Array[TSPPoint] = {
    (0 until n).map(_ => new TSPPoint(rand.nextDouble(), rand.nextDouble())).toArray
  }
  def main(args: Array[String]) {
    (4 until 50).foreach(numCities => {
      val cities = genCities(numCities)

      {
        val start = System.currentTimeMillis()
        val problem = new EuclideanTSP(cities)
        val sr = AStar.search(problem)
        if(sr != null) {
          val total = System.currentTimeMillis() - start
          println("A* cities: "+numCities+", Expanded: "+sr.expandedNodes+", Frontier: "+sr.frontierNodes+" cost: " +sr.result.pathCost() + " t: "+total+"ms")
          //println("     HCMiss = "+problem.hcMiss+" HCHit = "+problem.hcHit+ " computeTime="+problem.computeTime+ " routeLength:"+sr.result.state.asInstanceOf[TSPState].size)
          println(sr.result.state.asInstanceOf[TSPState].route)
        }
      }

      val start = System.currentTimeMillis()
      val problem2 = new EuclideanTSP(cities)
      val sr = AStar.bfs(problem2)
      if(sr != null) {
        val total = System.currentTimeMillis() - start
        println("BFS cities: "+numCities+", Expanded: "+sr.expandedNodes+", Frontier: "+sr.frontierNodes+" cost: " +sr.result.pathCost() + " t: "+total+"ms")
        //println("     HCMiss = "+problem2.hcMiss+" HCHit = "+problem2.hcHit+ " computeTime="+problem2.computeTime+ " routeLength:"+sr.result.state.asInstanceOf[TSPState].size)
        println(sr.result.state.asInstanceOf[TSPState].route)
      }
    })

  }
}

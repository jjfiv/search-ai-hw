package edu.umass.jfoley.ai

import scala.collection.mutable.ArrayBuffer

object TravelingSalesman {
  import SearchProblem._

  val rand = new scala.util.Random(13)

  def genCities(n: Int): Array[TSPPoint] = {
    (0 until n).map(_ => TSPPoint(rand.nextDouble(), rand.nextDouble())).toArray
  }
  def main(args: Array[String]) {
    (3 until 15).foreach(numCities => {
      val cities = genCities(numCities)
      greedy(TSPProblem(cities)) match {
        case FoundResult(n, numNodes) => {
          println("Greedy cities: "+numCities+", Expanded: "+numNodes)
        }
      }
      /*bfs(TSPProblem(cities)) match {
        case FoundResult(n, numNodes) => {
          println("BFS cities: "+numCities+", Expanded: "+numNodes)
        }
      }*/
    })

  }
}

case class TSPPoint(x: Double, y: Double)

case class TSPState(distance: Double, route: Seq[Int]) extends State {
  def currentCity = route.last
}
case class TSPAction(city: Int, distance: Double) extends Action {
  def cost() = distance
}

case class TSPProblem(cities: IndexedSeq[TSPPoint]) extends Problem {
  def start: State = TSPState(0.0, Seq(0)) // start at the "first" city
  val numCities = cities.size



  def isGoal(s: State): Boolean = s.asInstanceOf[TSPState].route.toSet.size == numCities

  def actions(from: State): Seq[Action] = {
    var idx = 0
    val trip = from.asInstanceOf[TSPState]
    val visited = trip.route.toSet
    val cur = trip.currentCity
    val curX = cities(cur).x
    val curY = cities(cur).y

    val actions = IndexedSeq.newBuilder[Action]
    actions.sizeHint(numCities-1)

    while(idx < cities.size) {
      if(!visited.contains(idx)) {
        val dest = cities(idx)
        val dx = dest.x - curX
        val dy = dest.y - curY

        val dist = Math.sqrt(dx*dx + dy*dy)
        actions += TSPAction(idx, dist)
      }
      idx += 1
    }
    actions.result()
  }

  def heuristic(from: State): Double = 1000

  def getNextState(from: State, action: Action): State = {
    val soFar = from.asInstanceOf[TSPState]
    val nextTrip = action.asInstanceOf[TSPAction]
    val totalDistance = soFar.distance + nextTrip.distance

    TSPState(totalDistance, soFar.route ++ Seq(nextTrip.city))
  }
}
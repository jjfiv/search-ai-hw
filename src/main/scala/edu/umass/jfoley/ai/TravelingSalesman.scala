package edu.umass.jfoley.ai

object TravelingSalesman {
  import SearchProblem._

  val rand = new scala.util.Random(13)

  def genCities(n: Int): Array[TSPPoint] = {
    (0 until n).map(_ => TSPPoint(rand.nextDouble(), rand.nextDouble())).toArray
  }
  def main(args: Array[String]) {
    (3 until 40).foreach(numCities => {
      val problem = TSPProblem(genCities(numCities))
      var start = System.currentTimeMillis()
      astar(problem) match {
        case FoundResult(n, numNodes) => {
          val total = System.currentTimeMillis() - start
          println("A* cities: "+numCities+", Expanded: "+numNodes+" t: "+total+"ms")
        }
      }
      if(numCities < 12) {
        start = System.currentTimeMillis()
        greedy(problem) match {
          case FoundResult(n, numNodes) => {
            val total = System.currentTimeMillis() - start
            println("Greedy cities: "+numCities+", Expanded: "+numNodes+" t: "+total+"ms")
          }
        }
      }
      if(numCities < 9) {
        start = System.currentTimeMillis()
        bfs(problem) match {
          case FoundResult(n, numNodes) => {
            val total = System.currentTimeMillis() - start
            println("BFS cities: "+numCities+", Expanded: "+numNodes+" t: "+total+"ms")
          }
        }
      }
    })

  }
}

case class TSPPoint(x: Double, y: Double)

case class TSPState(distance: Double, route: Seq[Int]) extends State {
  def currentCity = route.last
  def visited = route.toSet
}
case class TSPAction(city: Int, distance: Double) extends Action {
  def cost() = distance
}

case class MSTEdge(a: Int, b: Int, weight: Double)

// ugh, order matters
object CityEdge {
  def make(a: Int, b: Int) = if(a < b) CityEdge(a,b) else CityEdge(b,a)
}
case class CityEdge(src: Int, dest: Int)

case class TSPProblem(cities: IndexedSeq[TSPPoint]) extends Problem {
  def start: State = TSPState(0.0, Seq(0)) // start at the "first" city
  val numCities = cities.size
  val cityIds = (0 until numCities)

  // I hate scala collections
  val distances : Map[CityEdge, Double] = {
    val mb = Map.newBuilder[CityEdge, Double]
    var ii = 0
    while(ii < numCities) {
      val ci = cities(ii)
      val x1 = ci.x
      val y1 = ci.y

      var jj = ii+1
      while(jj < numCities) {
        val cj = cities(jj)
        val dx = x1 - cj.x
        val dy = y1 - cj.y

        val dist = Math.sqrt(dx*dx + dy*dy)
        mb += CityEdge(ii,jj) -> dist

        jj+= 1
      }
      ii+=1
    }
    mb.result()
  }
  val allEdges = distances.toSeq.map {
    case (CityEdge(a,b), weight) => MSTEdge(a,b,weight)
  }

  def isGoal(s: State): Boolean = s.asInstanceOf[TSPState].route.toSet.size == numCities

  def remainingCities(visited: Set[Int]) = {
    cityIds.filterNot(visited.contains)
  }

  def actions(from: State): Seq[Action] = {
    var idx = 0
    val trip = from.asInstanceOf[TSPState]
    val cur = trip.currentCity

    remainingCities(trip.visited).map(idx => {
      TSPAction(idx, distances(CityEdge.make(cur,idx)))
    })
  }

  def heuristic(from: State): Double = {
    val visited = from.asInstanceOf[TSPState].visited
    val rest = remainingCities(visited).toSet

    // drop any edges that lead backwards
    val edges = allEdges.filter(e => rest.contains(e.a) && rest.contains(e.b)).sortBy(_.weight)

    var weight = 0.0
    var groups = rest.zipWithIndex.toMap
    edges.foreach(edge => {
      val gA = groups(edge.a)
      val gB = groups(edge.b)
      if(gA != gB) {
        weight += edge.weight
        groups = groups.updated(edge.b, gA)
      }
    })

    return weight
  }

  def getNextState(from: State, action: Action): State = {
    val soFar = from.asInstanceOf[TSPState]
    val nextTrip = action.asInstanceOf[TSPAction]
    val totalDistance = soFar.distance + nextTrip.distance

    TSPState(totalDistance, soFar.route ++ Seq(nextTrip.city))
  }
}
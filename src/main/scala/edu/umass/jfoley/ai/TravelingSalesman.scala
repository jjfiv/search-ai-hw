package edu.umass.jfoley.ai

import gnu.trove.map.hash.TIntIntHashMap
import edu.umass.cs.jfoley.ai.{Action, State, AStar, SearchProblem}
import edu.umass.cs.jfoley.ai.tsp.TSPAction
import gnu.trove.set.hash.TIntHashSet
import gnu.trove.list.array.TIntArrayList
import edu.umass.cs.jfoley.ai.tsp.TSPState

object TravelingSalesman {
  val rand = new scala.util.Random(13)

  def genCities(n: Int): Array[TSPPoint] = {
    (0 until n).map(_ => TSPPoint(rand.nextDouble(), rand.nextDouble())).toArray
  }
  def main(args: Array[String]) {
    (35 until 400).foreach(numCities => {
      val cities = genCities(numCities)

      var start = System.currentTimeMillis()
      val problem2 = TSPProblem(cities)
      val sr = AStar.search(problem2)
      if(sr != null) {
        val total = System.currentTimeMillis() - start
        println("JAVA A* cities: "+numCities+", Expanded: "+sr.expandedNodes+", Frontier: "+sr.frontierNodes+" cost: " +sr.result.pathCost() + " t: "+total+"ms")
        println("     HCMiss = "+problem2.hcMiss+" HCHit = "+problem2.hcHit+ " computeTime="+problem2.computeTime+ " routeLength:"+sr.result.state.asInstanceOf[TSPState].route.size)
      }
    })

  }
}

case class TSPPoint(x: Double, y: Double)
case class MSTEdge(a: Int, b: Int, weight: Double)

// ugh, order matters
object CityEdge {
  def make(a: Int, b: Int) = if(a < b) CityEdge(a,b) else CityEdge(b,a)
}
case class CityEdge(src: Int, dest: Int)

case class TSPProblem(cities: IndexedSeq[TSPPoint]) extends SearchProblem {
  def start: State = new TSPState(0.0, new TIntArrayList(), cityIdSet) // start at the "first" city
  val numCities = cities.size
  val cityIds = 0 until numCities
  val cityIdSet = {
    val b = new TIntHashSet()
    b.addAll(cityIds.toArray)
    b
  }

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
  val allEdges = distances.toIndexedSeq.map {
    case (CityEdge(a,b), weight) => MSTEdge(a,b,weight)
  }.sortBy(_.a)

  def isGoal(s: State): Boolean = {
    val trip = s.asInstanceOf[TSPState]
    trip.size == numCities + 1
  }

  def remainingCities(visited: TIntArrayList): TIntHashSet = {
    val opposite = new TIntHashSet()
    cityIds.foreach(id => {
      if(!visited.contains(id)) {
        opposite.add(id)
      }
    })
    opposite.add(visited.getQuick(0))
    opposite
  }

  def actions(from: State): java.util.List[Action] = {
    val trip = from.asInstanceOf[TSPState]
    val res = new java.util.ArrayList[Action]

    if(trip.route.isEmpty) {
      cityIds.foreach(idx => {
        res.add(new TSPAction(idx, 0))
      })
    } else if(trip.route.size < numCities) {
      val cur = trip.currentCity
      cityIds.filterNot(trip.route.contains).foreach(idx => {
        res.add(new TSPAction(idx, distances(CityEdge.make(cur,idx))))
      })
    } else {
      val start = trip.firstCity
      val cur = trip.currentCity
      res.add(new TSPAction(start, distances(CityEdge.make(cur, start))))
    }

    res
  }

  var heuristicCache: Map[TIntHashSet, Double] = Map()
  var hcHit = 0
  var hcMiss = 0

  var computeTime = 0L

  def heuristic(from: State): Double = {
    val trip = from.asInstanceOf[TSPState]
    val rest = trip.remaining

    if(heuristicCache.contains(rest)) {
      hcHit += 1
      return heuristicCache(rest)
    }

    hcMiss += 1

    rest.size()
    //TODO generate edges instead of filtering
    val edges = new java.util.PriorityQueue[MSTEdge](100, new java.util.Comparator[MSTEdge] {
      def compare(a: MSTEdge, b: MSTEdge): Int = { (a.weight - b.weight).toInt }
    })
    val startTime = System.currentTimeMillis()

    // drop any edges that lead backwards
    var ei = 0
    while(ei < allEdges.size) {
      val curEdge = allEdges(ei)
      if(rest.contains(curEdge.a) && rest.contains(curEdge.b)) {
        edges.offer(curEdge)
      }
      ei += 1
    }

    computeTime += System.currentTimeMillis() - startTime

    var weight = 0.0
    var groups = new TIntIntHashMap()
    val others = rest.toArray
    var idx = 0
    while(idx < others.size) {
      groups.put(others(idx), idx)
      idx += 1
    }

    while(!edges.isEmpty) {
      val edge = edges.poll()
      val gA = groups.get(edge.a)
      val gB = groups.get(edge.b)
      if(gA != gB) {
        weight += edge.weight
        groups.put(edge.b, gA)
      }
    }

    heuristicCache = heuristicCache.updated(rest,weight)
    weight
  }

  def getNextState(from: State, action: Action): State = {
    val soFar = from.asInstanceOf[TSPState]
    val nextTrip = action.asInstanceOf[TSPAction]
    val totalDistance = soFar.distance + nextTrip.cost

    val route = new TIntArrayList()
    route.addAll(soFar.route)
    route.add(nextTrip.city)
    //val route = soFar.route ++ Seq(nextTrip.city)

    new TSPState(totalDistance, route, remainingCities(route))
  }
}
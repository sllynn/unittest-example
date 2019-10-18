package com.stuartdb.unittestexample

import org.apache.spark.sql.SparkSession
import org.scalatest.{BeforeAndAfterEach, FunSuite}

class aggregationFuncsTest extends FunSuite with BeforeAndAfterEach {

  var sparkSession: SparkSession = _

  override def beforeEach(): Unit = {
    sparkSession = SparkSession.builder()
      .master("local")
      .getOrCreate()

    sparkSession.sparkContext.setLogLevel("ERROR")
  }

  private def truncDouble(x: Double, dp: Int) = {
    val w = math.pow(10, dp)
    (x * w).toLong.toDouble / w
  }

  test("testAggregatePlayerStats") {
    val aggregator = new aggregationFuncs(sparkSession)

    val playerData = sparkSession.createDataFrame(
      Seq(
          (8, "Burnley", "Jeff Hendrick",             23,   5.4),
          (8, "Burnley", "Jack Cork",                 15,   5.0),
          (8, "Burnley", "Ben Mee",                   25,   5.0),
          (8, "Burnley", "Ashley Barnes",             34,   6.5),
          (8, "Burnley", "Steven Defour",             0,    5.5),
          (8, "Burnley", "Matthew Lowton",            28,   4.5),
          (8, "Burnley", "Jay Rodriguez",             11,   5.7),
          (8, "Burnley", "Ben Gibson",                0,    4.0),
          (8, "Burnley", "James Tarkowski",           26,   5.0),
          (8, "Burnley", "Ashley Westwood",           24,   5.4),
          (8, "Burnley", "Erik Pieters",              34,   4.8),
          (8, "Burnley", "Daniel Drinkwater",         0,    4.4),
          (8, "Burnley", "Phil Bardsley",             0,    4.4),
          (8, "Burnley", "Aaron Lennon",              6,    4.8),
          (8, "Burnley", "Nick Pope",                 32,   4.6),
          (8, "Burnley", "Johann Berg Gudmundsson",   17,   5.9),
          (8, "Burnley", "Joe Hart",                  0,    4.4),
          (8, "Burnley", "Charlie Taylor",            1,    4.2),
          (8, "Burnley", "Bailey Peacock Farrell",    0,    4.5),
          (8, "Burnley", "Dwight McNeil",             29,   6.0),
          (8, "Burnley", "Robbie Brady",              2,    5.5),
          (8, "Burnley", "Matej Vydra",               4,    5.4),
          (8, "Burnley", "Kevin Long",                0,    4.4),
          (8, "Burnley", "Chris Wood",                30,   6.2)
      )
    ).toDF("gameweek_id", "team_name", "player_name", "total_points", "current_value")

    val aggregatedTeamData = aggregator.aggregateTeamStats(playerData)
    aggregatedTeamData.show()
    assert(aggregatedTeamData.columns.length == 7)
    assert(aggregatedTeamData.count == 1)
    val aggregatedTeamDataLocal = aggregatedTeamData.collect()(0)
    assert(aggregatedTeamDataLocal(1) == "Burnley")
    assert(aggregatedTeamDataLocal(2) == 24)
    assert(aggregatedTeamDataLocal(3) == 341)
    assert(truncDouble(aggregatedTeamDataLocal(4).asInstanceOf[Double], 2) == 14.20)
    assert(truncDouble(aggregatedTeamDataLocal(5).asInstanceOf[Double], 2) == 121.50)
    assert(truncDouble(aggregatedTeamDataLocal(6).asInstanceOf[Double], 2) == 5.06)
  }

}

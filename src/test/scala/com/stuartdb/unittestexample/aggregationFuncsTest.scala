package com.stuartdb.unittestexample

import com.amazon.deequ.VerificationSuite
import com.amazon.deequ.checks.{Check, CheckLevel, CheckStatus}
import com.amazon.deequ.constraints.ConstrainableDataTypes
import org.apache.spark.sql.SparkSession
import org.scalatest.FunSuite

case class Player(gameWeekId: Long, teamName: String, playerName: String,
                   totalPoints: Long, currentValue: Double)

class aggregationFuncsTest extends FunSuite {

  lazy val spark: SparkSession =
    SparkSession.builder()
      .master("local")
      .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  private def truncDouble(x: Double, dp: Int) = {
    val w = math.pow(10, dp)
    (x * w).toLong.toDouble / w
  }

  test("testAggregatePlayerStats") {
    val aggregator = new aggregationFuncs(spark)

    val playerData = spark.createDataFrame(Seq(
      Player(8, "Burnley", "Jeff Hendrick",             23,   5.4),
      Player(8, "Burnley", "Jack Cork",                 15,   5.0),
      Player(8, "Burnley", "Ben Mee",                   25,   5.0),
      Player(8, "Burnley", "Ashley Barnes",             34,   6.5),
      Player(8, "Burnley", "Steven Defour",             0,    5.5),
      Player(8, "Burnley", "Matthew Lowton",            28,   4.5),
      Player(8, "Burnley", "Jay Rodriguez",             11,   5.7),
      Player(8, "Burnley", "Ben Gibson",                0,    4.0),
      Player(8, "Burnley", "James Tarkowski",           26,   5.0),
      Player(8, "Burnley", "Ashley Westwood",           24,   5.4),
      Player(8, "Burnley", "Erik Pieters",              34,   4.8),
      Player(8, "Burnley", "Daniel Drinkwater",         0,    4.4),
      Player(8, "Burnley", "Phil Bardsley",             0,    4.4),
      Player(8, "Burnley", "Aaron Lennon",              6,    4.8),
      Player(8, "Burnley", "Nick Pope",                 32,   4.6),
      Player(8, "Burnley", "Johann Berg Gudmundsson",   17,   5.9),
      Player(8, "Burnley", "Joe Hart",                  0,    4.4),
      Player(8, "Burnley", "Charlie Taylor",            1,    4.2),
      Player(8, "Burnley", "Bailey Peacock Farrell",    0,    4.5),
      Player(8, "Burnley", "Dwight McNeil",             29,   6.0),
      Player(8, "Burnley", "Robbie Brady",              2,    5.5),
      Player(8, "Burnley", "Matej Vydra",               4,    5.4),
      Player(8, "Burnley", "Kevin Long",                0,    4.4),
      Player(8, "Burnley", "Chris Wood",                30,   6.2)
      )
    ).toDF("gameweek_id", "team_name", "player_name", "total_points", "current_value")

    val aggregatedTeamData = aggregator.aggregateTeamStats(playerData)
    aggregatedTeamData.show()

    // 'classic' unit test using `assert()`
    assert(aggregatedTeamData.columns.length == 7)
    val aggregatedTeamDataLocal = aggregatedTeamData.collect()(0)
    assert(aggregatedTeamDataLocal(1) == "Burnley")
    assert(aggregatedTeamDataLocal(2) == 24)
    assert(aggregatedTeamDataLocal(3) == 341)
    assert(truncDouble(aggregatedTeamDataLocal(4).asInstanceOf[Double], 2) == 14.20)
    assert(truncDouble(aggregatedTeamDataLocal(5).asInstanceOf[Double], 2) == 121.50)
    assert(truncDouble(aggregatedTeamDataLocal(6).asInstanceOf[Double], 2) == 5.06)

    // unit test for output data validity / quality using deequ
    val verificationResult = VerificationSuite()
      .onData(aggregatedTeamData)
      .addCheck(
        Check(CheckLevel.Error, "Data verification test")
          .hasDataType("gameweek_id", ConstrainableDataTypes.Integral)
          .hasDataType("team_name", ConstrainableDataTypes.String)
          .hasDataType("total_points", ConstrainableDataTypes.Integral)
          .hasDataType("mean_points", ConstrainableDataTypes.Fractional)
          .hasSize(_ == 1)
          .isComplete("team_name")
          .isNonNegative("gameweek_id")
          .hasSum("player_count", _ == 24)
      ).run()

    assertResult(CheckStatus.Success)(verificationResult.status)

  }

}

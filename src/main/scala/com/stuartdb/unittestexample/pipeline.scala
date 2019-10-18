package com.stuartdb.unittestexample

import com.amazon.deequ.VerificationSuite
import com.amazon.deequ.checks.{Check, CheckLevel}
import org.apache.spark.sql.SparkSession


object pipeline {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local")
      .getOrCreate()

    spark.sparkContext.addJar("lib/deequ-1.0.2.jar")

    val players_by_gameweek = spark.read
      .parquet("/mnt/stuart/fpl/silver/players_gameweek")

    val verificationResult = VerificationSuite()
      .onData(players_by_gameweek)
      .addCheck(
        Check(CheckLevel.Error, "Data verification test")
          .isComplete("name") // should never be NULL
      ).run()

    val aggregator = new aggregationFuncs(spark)

    val team_stats = aggregator.aggregateTeamStats(players_by_gameweek)

    team_stats.write
      .format("parquet")
      .mode("overwrite")
      .save("/mnt/stuart/fpl/silver/stats")

    spark.sql(
      "CREATE TABLE IF NOT EXISTS fpl_silver.team_stats " +
        "( gameweek_id LONG" +
        ", team_name STRING" +
        ", player_count LONG" +
        ", total_points LONG" +
        ", mean_points DOUBLE" +
        ", total_current_value DOUBLE" +
        ", mean_current_value DOUBLE )" +
        "USING PARQUET " +
        "LOCATION '/mnt/stuart/fpl/silver/stats'"
    )
  }
}

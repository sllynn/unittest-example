package com.stuartdb.unittestexample

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.functions.{countDistinct, sum, avg}

class aggregationFuncs(spark: SparkSession) {

  def aggregateTeamStats(playerData: DataFrame): DataFrame = {
    import spark.implicits._
    playerData
      .groupBy("gameweek_id", "team_name")
      .agg(
        countDistinct('player_name) as "player_count",
        sum('total_points) as "total_points",
        avg('total_points) as "mean_points",
        sum('current_value) as "total_current_value",
        avg('current_value) as "mean_current_value"
      )
  }
}
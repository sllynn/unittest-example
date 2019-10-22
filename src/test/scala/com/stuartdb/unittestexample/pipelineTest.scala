package com.stuartdb.unittestexample

import org.scalatest.{FunSuite, PrivateMethodTester}
import com.amazon.deequ.VerificationSuite
import com.amazon.deequ.checks.{Check, CheckLevel, CheckStatus}
import com.amazon.deequ.constraints.ConstrainableDataTypes
import com.amazon.deequ.suggestions.{ConstraintSuggestionRunner, Rules}
import org.apache.spark.sql.{DataFrame, SparkSession}

class pipelineTest extends FunSuite with PrivateMethodTester {

  lazy val spark: SparkSession =
    SparkSession.builder()
      .master("local")
      .getOrCreate()
  spark.sparkContext.setLogLevel("ERROR")

  test("testPipeline") {
    import spark.implicits._
    val pl = pipeline
    val pipelineExecute = PrivateMethod[DataFrame]('execute)

    val outputDF = pl invokePrivate pipelineExecute(
      "/mnt/stuart/fpl/silver/players_gameweek",
      "/mnt/stuart/fpl/silver/stats", spark
    )

    // We ask deequ to compute constraint suggestions for us on the data
    val suggestionResult = { ConstraintSuggestionRunner()
      // data to suggest constraints for
      .onData(outputDF)
      // default set of rules for constraint suggestion
      .addConstraintRules(Rules.DEFAULT)
      // run data profiling and constraint suggestion
      .run()
    }

    // We can now investigate the constraints that Deequ suggested.
    val suggestionDataFrame = suggestionResult.constraintSuggestions.flatMap {
      case (column, suggestions) =>
        suggestions.map { constraint =>
          (column, constraint.description, constraint.codeForConstraint)
        }
    }.toSeq.toDS()

    suggestionDataFrame.show(truncate=false)

    // Finally we apply some of these constraints to our data
    val verificationResult = VerificationSuite()
      .onData(outputDF)
      .addCheck(
        Check(CheckLevel.Error, "Data verification test")
          .hasDataType("team_name", ConstrainableDataTypes.String)
          .hasDataType("total_points", ConstrainableDataTypes.Integral)
          .hasDataType("mean_points", ConstrainableDataTypes.Fractional)
          .hasSize(_ >= 20)
          .isComplete("team_name")
          .isNonNegative("gameweek_id")
          .hasMin("player_count", _ >= 11)
      ).run()

    assertResult(CheckStatus.Success)(verificationResult.status)

  }

}

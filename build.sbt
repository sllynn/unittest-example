name := "unittest-example"

version := "0.1"

scalaVersion := "2.11.12"
val spark_libs = 
  sys.env.getOrElse("SPARK_JARS", "/Users/stuartlynn/miniconda3/envs/dbconnect/lib/python3.5/site-packages/pyspark/jars")
unmanagedBase := new java.io.File(spark_libs)
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "com.amazon.deequ" % "deequ" % "1.0.2"

mainClass := Some("pipeline")

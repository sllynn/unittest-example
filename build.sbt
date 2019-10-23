name := "unittest-example"

version := "0.1"

scalaVersion := "2.11.12"

unmanagedBase := new java.io.File(System.getenv("SPARK_JARS"))
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
libraryDependencies += "com.amazon.deequ" % "deequ" % "1.0.2"

mainClass := Some("pipeline")

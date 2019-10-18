name := "unittest-example"

version := "0.1"

scalaVersion := "2.11.12"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.8" % "test"
unmanagedBase := new java.io.File("/Users/stuartlynn/miniconda3/envs/dbconnect/lib/python3.5/site-packages/pyspark/jars")
libraryDependencies += "com.amazon.deequ" % "deequ" % "1.0.2"
//assemblyMergeStrategy in assembly := {
////  case PathList("lib", xs @ _*) => MergeStrategy.first
//  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
//  case PathList("META-INF", xs @ _*) => MergeStrategy.first
//  case x => MergeStrategy.discard
//}

mainClass := Some("pipeline")

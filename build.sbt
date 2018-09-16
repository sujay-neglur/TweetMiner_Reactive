name := """play-java-starter-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.12.6"

crossScalaVersions := Seq("2.11.12", "2.12.4")

libraryDependencies += guice

// Test Database
libraryDependencies += "com.h2database" % "h2" % "1.4.197"

// Testing libraries for dealing with CompletionStage...
libraryDependencies += "org.assertj" % "assertj-core" % "3.6.2" % Test
libraryDependencies += "org.awaitility" % "awaitility" % "2.0.0" % Test

// Make verbose tests

libraryDependencies ++= Seq(
  "org.twitter4j" % "twitter4j-core" % "4.0.4",
  "org.twitter4j" % "twitter4j-async" % "4.0.4",
  "org.twitter4j" % "twitter4j-stream" % "4.0.4",
  "org.twitter4j" % "twitter4j-media-support" % "4.0.4"
)
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5.11" % Test
//libraryDependencies +="com.typesafe.play" %% "play-ahc-ws" % "2.11:2.6.17" % Test

testOptions in Test := Seq(Tests.Argument(TestFrameworks.JUnit, "-a", "-v"))

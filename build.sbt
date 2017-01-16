name := "conf-http-api"

scalaVersion := "2.11.8"

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
unmanagedSourceDirectories in Test := Seq((scalaSource in Test).value)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.0",
  "de.heikoseeberger" %% "akka-http-circe" % "1.11.0",
  "io.circe" %% "circe-core" % "0.6.1",
  "io.circe" %% "circe-generic" % "0.6.1",
  "io.circe" %% "circe-parser" % "0.6.1",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test"
)

scalacOptions := Seq("-unchecked",
                     "-deprecation",
                     "-encoding", "utf8",
                     "-feature",
                     "-language:implicitConversions",
                     "-language:higherKinds"
                     )


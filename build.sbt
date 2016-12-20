name := "conf-http-api"

scalaVersion := "2.11.8"

unmanagedSourceDirectories in Compile := Seq((scalaSource in Compile).value)
unmanagedSourceDirectories in Test := Seq((scalaSource in Compile).value)

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.0",
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

scalacOptions := Seq("-unchecked",
                     "-deprecation",
                     "-encoding", "utf8",
                     "-feature",
                     "-language:implicitConversions",
                     "-language:higherKinds"
                     )


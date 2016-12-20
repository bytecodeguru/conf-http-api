name := "conf-http-api"

scalaVersion := "2.11.8"

unmanagedSourceDirectories in Compile <<= Seq(scalaSource in Compile).join

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % "10.0.0"
)

scalacOptions := Seq("-unchecked",
                     "-deprecation",
                     "-encoding", "utf8",
                     "-feature",
                     "-language:implicitConversions",
                     "-language:higherKinds"
                     )


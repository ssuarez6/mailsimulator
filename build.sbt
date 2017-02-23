name := "mailsimulator"

version := "0.1"

scalaVersion := "2.12.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.17"
cancelable in Global := true //permite ctrl-c sin salir de sbt

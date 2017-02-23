name := "mailsimulator"

version := "0.1"

resolvers += "Typesafe Repository" at "http://repo.typesafe.com/typesafe/releases/"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.0.1"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.1" % "test"
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.8"
libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.17"
libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.5-M1"
cancelable in Global := true //permite ctrl-c sin salir de sbt

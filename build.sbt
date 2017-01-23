name := "playground"

version := "1.0"
scalaVersion := "2.12.1"

resolvers in ThisBuild ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

val ammonite = Seq(
  libraryDependencies += "com.lihaoyi" % "ammonite" % "0.8.1" % "test" cross CrossVersion.full,
  initialCommands in (Test, console) := """ammonite.Main().run()"""
)

////////////////////

lazy val playShapeless = Project(id = "playground-shapeless", base = file("modules/playground-shapeless"))
  .settings(
    ammonite,

    scalaVersion := "2.12.0",
    scalaOrganization := "org.typelevel",

    libraryDependencies ++= Seq(
      "com.chuusai" %% "shapeless" % "2.3.2"
    )
  )

lazy val playFigaro = Project(id = "playground-figaro", base = file("modules/playground-figaro"))
  .settings(
    ammonite,

    scalaVersion := "2.11.8",

    libraryDependencies ++= Seq(
      "com.cra.figaro" %% "figaro" % "4.1.0.0" // publish local https://github.com/p2t2/figaro, latest branch
      ,"org.scalatest" %% "scalatest" % "3.0.1" % Test
    )
  )


val akkaVersion = "2.4.16"
lazy val playAkka = Project(id = "playground-akka", base = file("modules/playground-akka"))
  .settings(
    ammonite,

    scalaVersion := "2.11.8",

    libraryDependencies ++= Seq(
  //    "com.github.krasserm" %% "streamz-converter" % "0.8-SNAPSHOT",

      "org.tpolecat" %% "doobie-core-cats" % "0.4.1",
      "org.tpolecat" %% "doobie-postgres-cats" % "0.4.1",


      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-agent" % akkaVersion,
      "com.typesafe.akka" %% "akka-camel" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-sharding" % akkaVersion,
      "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
      "com.typesafe.akka" %% "akka-contrib" % akkaVersion,
      "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-osgi" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-tck" % akkaVersion,
      "com.typesafe.akka" %% "akka-remote" % akkaVersion,
      "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
      "com.typesafe.akka" %% "akka-distributed-data-experimental" % akkaVersion,
      "com.typesafe.akka" %% "akka-typed-experimental" % akkaVersion,
      "com.typesafe.akka" %% "akka-persistence-query-experimental" % akkaVersion
      ,"org.scalatest" %% "scalatest" % "3.0.1" % Test
    )
  )

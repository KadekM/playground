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

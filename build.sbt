name := "playground"

version := "1.0"

scalaVersion := "2.12.0"

scalaOrganization := "org.typelevel"

resolvers ++= Seq(
  Resolver.sonatypeRepo("releases"),
  Resolver.sonatypeRepo("snapshots")
)

libraryDependencies ++= Seq(
  "com.chuusai" %% "shapeless" % "2.3.2"
)


ThisBuild / version := "1.0.0"

ThisBuild / scalaVersion := "3.3.1"

lazy val commonSettings = Seq(
  name := "OneLib",
  sourceDirectory := file("./src/"),
  idePackagePrefix.withRank(KeyRanks.Invisible) := Some("dev.turtle.onelib"),
  assembly / mainClass := Some("dev.turtle.onelib.OneLib"),
  assembly / assemblyJarName := name + "-" + version + ".jar",
  assembly / assemblyOutputPath := file(s"S:\\mc_server\\plugins\\${name.value}.jar")
)
ThisBuild / resolvers ++= Seq(
  "Spigot Snapshots" at "https://hub.spigotmc.org/nexus/content/repositories/snapshots"
)

ThisBuild / libraryDependencies ++= Seq(
  "org.spigotmc" % "spigot-api" % "1.20.2-R0.1-SNAPSHOT" % "provided",
  "com.typesafe" % "config" % "1.4.2"
)
lazy val core = project.in(file("."))
  .settings(commonSettings:_*)
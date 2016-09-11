scalaVersion := "2.11.8"

val scalazVersion = "7.2.6"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazVersion
)

scalacOptions += "-feature"

initialize ~= { _ =>
  val ansi = System.getProperty("sbt.log.noformat", "false") != "true"
  if (ansi) System.setProperty("scala.color", "true")
}

initialCommands in console := "import scalaz._, Scalaz._"

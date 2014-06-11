import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

  val appName         = "modeling_front"
  val appVersion      = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    // Add your project dependencies here,
    jdbc,
    anorm,
    "com.typesafe.akka" %% "akka-actor" % "2.2.3"
  )


  val main = play.Project(appName, appVersion, appDependencies).settings(
    // Add your own project settings here      
  )

}

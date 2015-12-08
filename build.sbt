import scalariform.formatter.preferences._

organization := "com.github.edgecaseberg"

name := "how-to-test-mailerplugin"

version := "0.0.0" 

scalaVersion := "2.11.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

resolvers ++= Seq(
	"Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/")

libraryDependencies ++= { 
	Seq(
		"com.typesafe" % "config" % "1.2.1",
		"com.sun.mail" % "javax.mail" % "1.5.4",
		"com.typesafe.play.plugins" %% "play-plugins-mailer" % "2.3.1",
		"org.scalatest" % "scalatest_2.11" % "2.2.4" % "test",
		"com.icegreen" % "greenmail" % "1.4.1" % "test"
	)
}

scalariformPreferences := scalariformPreferences.value
  .setPreference(DoubleIndentClassDeclaration, true)
  .setPreference(PreserveDanglingCloseParenthesis, true)
  .setPreference(AlignParameters, false)
  .setPreference(IndentWithTabs, true)
  .setPreference(MultilineScaladocCommentsStartOnFirstLine, true)
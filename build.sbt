name := """play-twitter-sample"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava,PlayEbean)

scalaVersion := "2.11.8"
unmanagedBase := baseDirectory.value / "lib"

//キャッシュが残るのでバージョン変更時は
// activator clean-> activator run
libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  evolutions,
  "commons-codec" % "commons-codec" % "1.10",
  "junit" % "junit" % "4.12",
  "org.webjars" %% "webjars-play" % "2.5.0",
  "com.h2database" % "h2" % "1.4.191",
  "com.loicdescotte.coffeebean" %% "html5tags" % "1.2.2",
  filters
)
herokuAppName in Compile := "asciiart-converter"

routesGenerator:=InjectedRoutesGenerator
resolvers+="webjars" at "http://webjars.github.com/m2"

// avoid an error "your input is too long " in windows environment
import com.typesafe.sbt.packager.Keys.scriptClasspath
scriptClasspath := {
  val originalClasspath = scriptClasspath.value
  val manifest = new java.util.jar.Manifest()
  manifest.getMainAttributes().putValue("Class-Path", originalClasspath.mkString(" "))
  val classpathJar = (target in Universal).value / "lib" / "classpath.jar"
  IO.jar(Seq.empty, classpathJar, manifest)
  Seq(classpathJar.getName)
}
mappings in Universal += (((target in Universal).value / "lib" / "classpath.jar") -> "lib/classpath.jar")

//pipelineStages:= Seq(rjs, digest, gzip)

// client side validation with play form helper
// https://github.com/loicdescotte/Play2-HTML5Tags
resolvers += Resolver.url("github repo for html5tags", url("http://loicdescotte.github.io/Play2-HTML5Tags/releases/"))(Resolver.ivyStylePatterns)

//Reload失敗対策
//PlayKeys.playWatchService := play.sbtplugin.run.PlayWatchService.sbt(pollInterval.value)
fork in run := false
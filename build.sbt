name := "sputter-demo"

lazy val commonSettings = Seq(
  organization := "com.github.boosh",

  version := "0.1.0-SNAPSHOT",

  scalaVersion := "2.11.8",

  scalacOptions ++= Seq("-deprecation", "-feature"),

  scalacOptions in Test ++= Seq("-Yrangepos"),

  libraryDependencies ++= Seq(
    "org.scalatest" % "scalatest_2.11" % "3.0.0-RC4" % "test"
  )
)

resolvers += Resolver.mavenLocal


lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(Seq(
    publish := {},
    publishLocal := {}
  ))
  .aggregate(sputterDemoJvm, sputterDemoJs)


// depend on the local library source
lazy val sputter = uri("../sputter")

val webAssetsDir = "js/assets/"


lazy val sputterDemo = crossProject.in(file("."))
  .settings(
    name := "sputter-demo"
  )
  .jvmConfigure(_.dependsOn(ProjectRef(sputter, "sputterJVM")))
  .jsConfigure(_.dependsOn(ProjectRef(sputter, "sputterJS")))
  .jvmSettings(commonSettings: _*)
  .jvmSettings(
    libraryDependencies ++= Seq(
      "com.typesafe" % "config" % "1.3.0",
      "com.typesafe.akka" %% "akka-actor" % "2.4.8",
      "com.typesafe.akka" % "akka-http-experimental_2.11" % "2.4.8",
      "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.8",
      "com.typesafe.akka" %% "akka-slf4j" % "2.4.8",

      // logging
      "org.slf4j" % "slf4j-api" % "1.7.21",
      "org.slf4j" % "slf4j-log4j12" % "1.7.21",

      // type-safe REST calls
      "com.lihaoyi" %% "autowire" % "0.2.5",
      "com.lihaoyi" %% "upickle" % "0.4.1",

      // utils
      "commons-io" % "commons-io" % "2.4",
      "org.jsoup" % "jsoup" % "1.9.2",
      "org.scalaz" %% "scalaz-core" % "7.2.3"
    )
  )
  .jsSettings(commonSettings: _*)
  .jsSettings(
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "upickle" % "0.4.1",
      "com.lihaoyi" %%% "autowire" % "0.2.5",
      "com.github.chandu0101.sri" %%% "web" % "0.5.0",
      "com.github.chandu0101.sri" %%% "scalacss" % "2016.5.0"
    ),

    crossTarget in(Compile, fullOptJS) := file(webAssetsDir),
    crossTarget in(Compile, fastOptJS) := file(webAssetsDir),
    crossTarget in(Compile, packageScalaJSLauncher) := file(webAssetsDir),
    artifactPath in(Compile, fastOptJS) := ((crossTarget in(Compile, fastOptJS)).value /
      ((moduleName in fastOptJS).value + "-opt.js")),

    scalacOptions ++= Seq("-deprecation", "-unchecked", "-feature",
      "-language:postfixOps", "-language:implicitConversions",
      "-language:higherKinds", "-language:existentials")
  )


lazy val sputterDemoJvm = sputterDemo.jvm.dependsOn(sputter)
lazy val sputterDemoJs = sputterDemo.js.dependsOn(sputter)

///////// Demo settings /////////

//// demo server
//lazy val demo_jvm = project.settings(commonSettings: _*)
//  .dependsOn(sputterDemoJvm)

// demo web app settings
// copy fastOptJS/fullOptJS  files to assets directory
//val webAssetsDir = "demo_client_web/assets/"
//
//lazy val demo_client_web = project.settings(commonSettings: _*)
//  .enablePlugins(ScalaJSPlugin)
//  .dependsOn(sputterJs)
//  .settings(Seq(
//    libraryDependencies ++= Seq(
//      "com.github.chandu0101.sri" %%% "web" % "0.5.0",
//      "com.github.chandu0101.sri" %%% "scalacss" % "2016.5.0"
//    ),
//
//
//  ))

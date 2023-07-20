// *****************************************************************************
// Projects
// *****************************************************************************
import Dependencies.pureLibs

lazy val pure =
  project
    .in(file("pure"))
    .settings(settings)
    .settings(
      libraryDependencies ++= pureLibs
    )

lazy val it =
  project
    .in(file("pure-it"))
    .settings(settings)
    .dependsOn(pure)

lazy val root =
  project
    .in(file("."))
    .settings(settings)
    .aggregate(pure, it)

// *****************************************************************************
// Settings
// *****************************************************************************

lazy val settings =
  commonSettings ++ scalafmtSettings

ThisBuild / scalacOptions ++=
  Seq(
    "-deprecation",
    "-explain",
    "-Wunused:imports",
    "-unchecked",
    "-Xfatal-warnings",
    "-feature"
  )

lazy val commonSettings =
  Seq(
    scalaVersion                         := "3.3.0",
    organization                         := "com.wegtam",
    organizationName                     := "Jens Grassel",
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Compile / compile / wartremoverWarnings ++= Warts.unsafe,
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )

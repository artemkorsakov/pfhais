// *****************************************************************************
// Projects
// *****************************************************************************
import Dependencies.pureLibs

lazy val pure =
  project
    .in(file("pure"))
    .enablePlugins(AutomateHeaderPlugin)
    .settings(settings)
    .settings(
      libraryDependencies ++= pureLibs
    )

lazy val it =
  project
    .in(file("pure-it"))
    .enablePlugins(AutomateHeaderPlugin)
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
  commonSettings ++
    scalafmtSettings

val licenseText = s"""CC0 1.0 Universal (CC0 1.0) - Public Domain Dedication
                   |
                   |                               No Copyright
                   |
                   |The person who associated a work with this deed has dedicated the work to
                   |the public domain by waiving all of his or her rights to the work worldwide
                   |under copyright law, including all related and neighboring rights, to the
                   |extent allowed by law.""".stripMargin

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
    startYear                            := Some(2019),
    headerLicense                        := Some(HeaderLicense.Custom(licenseText)),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Compile / compile / wartremoverWarnings ++= Warts.unsafe,
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )

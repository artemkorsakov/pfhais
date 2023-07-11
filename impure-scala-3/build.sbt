// *****************************************************************************
// Projects
// *****************************************************************************

lazy val impure =
  project
    .in(file("."))
    .enablePlugins(AutomateHeaderPlugin)
    .configs(IntegrationTest)
    .settings(settings)
    .settings(
      Defaults.itSettings,
      headerSettings(IntegrationTest),
      inConfig(IntegrationTest)(scalafmtSettings),
      IntegrationTest / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
      IntegrationTest / parallelExecution          := false,
      IntegrationTest / unmanagedSourceDirectories := Seq((IntegrationTest / scalaSource).value)
    )
    .settings(
      libraryDependencies ++= Seq(
        library.akkaActor,
        library.akkaHttp,
//        library.akkaHttpJson,
        library.akkaSlf4j,
        library.akkaStream,
        library.catsCore,
        library.circeCore,
        library.circeGeneric,
        library.circeParser,
        library.flywayCore,
        library.ironCats,
        library.ironCirce,
        library.ironCore,
        library.logback,
        library.postgresql,
        library.slick,
        library.slickHikariCP,
        library.akkaHttpTestkit   % IntegrationTest,
        library.akkaStreamTestkit % IntegrationTest,
        library.akkaTestkit       % IntegrationTest,
        library.ironScalaCheck    % IntegrationTest,
        library.scalaCheck        % IntegrationTest,
        library.scalaTest         % IntegrationTest,
        library.akkaHttpTestkit   % Test,
        library.akkaStreamTestkit % Test,
        library.akkaTestkit       % Test,
        library.ironScalaCheck    % Test,
        library.scalaCheck        % Test,
        library.scalaTest         % Test
      )
    )

// *****************************************************************************
// Library dependencies
// *****************************************************************************

lazy val library =
  new {
    object Version {
      val akka         = "2.8.3"
      val akkaHttp     = "10.5.2"
      val akkaHttpJson = "1.39.2"
      val cats         = "2.9.0"
      val circe        = "0.14.5"
      val flyway       = "9.20.0"
      val iron         = "2.1.0"
      val logback      = "1.4.8"
      val postgresql   = "42.6.0"
      val scalaCheck   = "1.17.0"
      val scalaTest    = "3.2.16"
      val slick        = "3.5.0-M4"
    }
    val akkaActor   = "com.typesafe.akka" %% "akka-actor"   % Version.akka
    val akkaTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.akka
    val akkaHttp    = "com.typesafe.akka" %% "akka-http"    % Version.akkaHttp
//    val akkaHttpJson      = ("de.heikoseeberger"  %% "akka-http-circe"     % Version.akkaHttpJson).cross(CrossVersion.for3Use2_13)
    val akkaHttpTestkit   = "com.typesafe.akka"  %% "akka-http-testkit"   % Version.akkaHttp
    val akkaSlf4j         = "com.typesafe.akka"  %% "akka-slf4j"          % Version.akka
    val akkaStream        = "com.typesafe.akka"  %% "akka-stream"         % Version.akka
    val akkaStreamTestkit = "com.typesafe.akka"  %% "akka-stream-testkit" % Version.akka
    val catsCore          = "org.typelevel"      %% "cats-core"           % Version.cats
    val circeCore         = "io.circe"           %% "circe-core"          % Version.circe
    val circeGeneric      = "io.circe"           %% "circe-generic"       % Version.circe
    val circeParser       = "io.circe"           %% "circe-parser"        % Version.circe
    val flywayCore        = "org.flywaydb"        % "flyway-core"         % Version.flyway
    val ironCore          = "io.github.iltotore" %% "iron"                % Version.iron
    val ironCats          = "io.github.iltotore" %% "iron-cats"           % Version.iron
    val ironCirce         = "io.github.iltotore" %% "iron-circe"          % Version.iron
    val ironScalaCheck    = "io.github.iltotore" %% "iron-scalacheck"     % Version.iron
    val logback           = "ch.qos.logback"      % "logback-classic"     % Version.logback
    val postgresql        = "org.postgresql"      % "postgresql"          % Version.postgresql
    val scalaCheck        = "org.scalacheck"     %% "scalacheck"          % Version.scalaCheck
    val scalaTest         = "org.scalatest"      %% "scalatest"           % Version.scalaTest
    val slick             = "com.typesafe.slick" %% "slick"               % Version.slick
    val slickHikariCP     = "com.typesafe.slick" %% "slick-hikaricp"      % Version.slick
  }

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
    scalaVersion     := "3.3.0",
    organization     := "com.wegtam",
    organizationName := "Jens Grassel",
    startYear        := Some(2019),
    headerLicense    := Some(HeaderLicense.Custom(licenseText)),
    Compile / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
    Compile / unmanagedSourceDirectories := Seq((Compile / scalaSource).value),
    Compile / compile / wartremoverWarnings ++= Warts.unsafe,
    Test / console / scalacOptions --= Seq("-Xfatal-warnings", "-Ywarn-unused-import"),
    Test / unmanagedSourceDirectories := Seq((Test / scalaSource).value)
  )

lazy val scalafmtSettings =
  Seq(
    scalafmtOnCompile := true
  )

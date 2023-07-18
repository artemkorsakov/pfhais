import sbt.*

object Dependencies {

  private lazy val library =
    new {
      object Version {
        val cats       = "2.9.0"
        val circe      = "0.14.5"
        val doobie     = "1.0.0-RC4"
        val flyway     = "9.20.1"
        val http4s     = "0.23.22"
        val iron       = "2.1.0"
        val kittens    = "3.0.0"
        val logback    = "1.4.8"
        val postgresql = "42.6.0"
        val pureConfig = "0.17.4"
        val scalaCheck = "1.17.0"
        val scalaTest  = "3.2.16"
      }

      val catsCore          = "org.typelevel"         %% "cats-core"           % Version.cats
      val circeCore         = "io.circe"              %% "circe-core"          % Version.circe
      val circeGeneric      = "io.circe"              %% "circe-generic"       % Version.circe
      val circeParser       = "io.circe"              %% "circe-parser"        % Version.circe
      val doobieCore        = "org.tpolecat"          %% "doobie-core"         % Version.doobie
      val doobieHikari      = "org.tpolecat"          %% "doobie-hikari"       % Version.doobie
      val doobiePostgres    = "org.tpolecat"          %% "doobie-postgres"     % Version.doobie
      val doobieRefined     = "org.tpolecat"          %% "doobie-refined"      % Version.doobie
      val doobieScalaTest   = "org.tpolecat"          %% "doobie-scalatest"    % Version.doobie
      val flywayCore        = "org.flywaydb"           % "flyway-core"         % Version.flyway
      val http4sCirce       = "org.http4s"            %% "http4s-circe"        % Version.http4s
      val http4sDsl         = "org.http4s"            %% "http4s-dsl"          % Version.http4s
      val http4sEmberClient = "org.http4s"            %% "http4s-ember-client" % Version.http4s
      val http4sEmberServer = "org.http4s"            %% "http4s-ember-server" % Version.http4s
      val ironCore          = "io.github.iltotore"    %% "iron"                % Version.iron
      val ironCats          = "io.github.iltotore"    %% "iron-cats"           % Version.iron
      val ironCirce         = "io.github.iltotore"    %% "iron-circe"          % Version.iron
      val ironScalaCheck    = "io.github.iltotore"    %% "iron-scalacheck"     % Version.iron
      val kittens           = "org.typelevel"         %% "kittens"             % Version.kittens
      val logback           = "ch.qos.logback"         % "logback-classic"     % Version.logback
      val postgresql        = "org.postgresql"         % "postgresql"          % Version.postgresql
      val pureConfig        = "com.github.pureconfig" %% "pureconfig-core"     % Version.pureConfig
      val scalaCheck        = "org.scalacheck"        %% "scalacheck"          % Version.scalaCheck
      val scalaTest         = "org.scalatest"         %% "scalatest"           % Version.scalaTest
    }

  lazy val pureLibs: Seq[ModuleID] = Seq(
    library.catsCore,
    library.circeCore,
    library.circeGeneric,
    library.circeParser,
    library.doobieCore,
    library.doobieHikari,
    library.doobiePostgres,
    library.doobieRefined,
    library.flywayCore,
    library.http4sEmberClient,
    library.http4sEmberServer,
    library.http4sCirce,
    library.http4sDsl,
    library.kittens,
    library.logback,
    library.postgresql,
    library.pureConfig,
    library.ironCats,
    library.ironCore,
    library.ironCirce,
    library.doobieScalaTest % Test,
    library.ironScalaCheck % Test,
    library.scalaCheck % Test,
    library.scalaTest % Test
  )
}

/*
 * CC0 1.0 Universal (CC0 1.0) - Public Domain Dedication
 *
 *                                No Copyright
 *
 * The person who associated a work with this deed has dedicated the work to
 * the public domain by waiving all of his or her rights to the work worldwide
 * under copyright law, including all related and neighboring rights, to the
 * extent allowed by law.
 */

package com.wegtam.books.pfhais

import cats.effect.*
import cats.effect.unsafe.implicits.global
import cats.implicits.*
import com.comcast.ip4s.*
import com.typesafe.config.*
import com.wegtam.books.pfhais.api.*
import com.wegtam.books.pfhais.config.*
import com.wegtam.books.pfhais.db.*
import doobie.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.Router
import pureconfig.*

import scala.io.StdIn

object Pure extends IOApp:
  @SuppressWarnings(Array("org.wartremover.warts.Any", "scalafix:DisableSyntax.null"))
  def run(args: List[String]): IO[ExitCode] =
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator

    val configsIO =
      for
        cfg       <- IO(ConfigFactory.load(getClass.getClassLoader))
        apiConfig <- loadConfig[ApiConfig](cfg, "api")
        dbConfig  <- loadConfig[DatabaseConfig](cfg, "database")
      yield (apiConfig, dbConfig)

    val program =
      for
        configs <- configsIO
        (apiConfig, dbConfig) = configs
        _ <- migrator.migrate(dbConfig.url, dbConfig.user, dbConfig.pass)
        host <- IO.fromOption(Host.fromString(apiConfig.host))(
          new IllegalArgumentException("Invalid host")
        )
        port <- IO.fromOption(Port.fromInt(apiConfig.port.toInt))(
          new IllegalArgumentException("Invalid port")
        )
      yield
        val tx = Transactor.fromDriverManager[IO](
          driver = dbConfig.driver.toString,
          url = dbConfig.url.toString,
          user = dbConfig.user.toString,
          password = dbConfig.pass.toString,
          logHandler = None
        )
        val repo           = new DoobieRepository(tx)
        val productRoutes  = new ProductRoutes(repo)
        val productsRoutes = new ProductsRoutes(repo)
        val routes         = productRoutes.routes <+> productsRoutes.routes
        val httpApp        = Router("/" -> routes).orNotFound
        val server = EmberServerBuilder
          .default[IO]
          .withHost(host)
          .withPort(port)
          .withHttpApp(httpApp)
        server.build.use(_ => IO(StdIn.readLine())).as(ExitCode.Success)

    program.attempt.unsafeRunSync() match
      case Left(e) =>
        IO {
          println("*** An error occured! ***")
          if e ne null then println(e.getMessage)
          ExitCode.Error
        }
      case Right(r) => r

  private def loadConfig[A: ConfigReader](cfg: Config, namespace: String): IO[A] =
    val result = ConfigSource.fromConfig(cfg).at(namespace).load[A]
    IO.fromEither(result.left.map(error => new IllegalArgumentException(error.prettyPrint())))

end Pure

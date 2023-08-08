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

package com.wegtam.books.pfhais.db

import cats.effect.IO
import cats.effect.unsafe.implicits.global
import com.typesafe.config.*
import com.wegtam.books.pfhais.config.*
import munit.*
import org.flywaydb.core.Flyway
import pureconfig.ConfigSource
import io.github.iltotore.iron.*
import org.flywaydb.core.api.FlywayException

final class FlywayDatabaseMigratorSuite extends FunSuite:
  val dbConfig = new Fixture[DatabaseConfig]("configs") {
    private val config   = ConfigFactory.load()
    private val dbConfig = ConfigSource.fromConfig(config).at("database").load[DatabaseConfig]

    def apply(): DatabaseConfig =
      dbConfig.getOrElse(throw new IllegalArgumentException("Invalid config"))

    override def beforeEach(context: BeforeEach): Unit =
      dbConfig.foreach: cfg =>
        val flyway: Flyway = Flyway.configure().dataSource(cfg.url, cfg.user, cfg.pass).load()
        val _              = flyway.migrate()
        flyway.clean()

    override def afterEach(context: AfterEach): Unit =
      dbConfig.foreach: cfg =>
        val flyway: Flyway = Flyway.configure().dataSource(cfg.url, cfg.user, cfg.pass).load()
        flyway.clean()
  }

  override def munitFixtures = List(dbConfig)

  test(
    "FlywayDatabaseMigrator#migrate when the database is configured and available when the database is not up to date must return the number of applied migrations"
  ) {
    val cfg                            = dbConfig()
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
    val program                        = migrator.migrate(cfg.url, cfg.user, cfg.pass)
    assert(program.unsafeRunSync() > 0)
  }

  test(
    "FlywayDatabaseMigrator#migrate when the database is configured and available when the database is up to date must return zero"
  ) {
    val cfg                            = dbConfig()
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
    val program                        = migrator.migrate(cfg.url, cfg.user, cfg.pass)
    val _                              = program.unsafeRunSync()
    assertEquals(0, program.unsafeRunSync())
  }

  test(
    "FlywayDatabaseMigrator#migrate when the database is not available must throw an exception"
  ) {
    val cfg = DatabaseConfig(
      driver = "This is no driver name!",
      url = "jdbc://some.host/whatever",
      user = "no-user",
      pass = "no-password"
    )
    val migrator: DatabaseMigrator[IO] = new FlywayDatabaseMigrator
    val program                        = migrator.migrate(cfg.url, cfg.user, cfg.pass)
    intercept[FlywayException] {
      program.unsafeRunSync()
    }
  }

end FlywayDatabaseMigratorSuite
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

package com.wegtam.books.pfhais.config

import com.typesafe.config.*
import com.wegtam.books.pfhais.config.DatabaseConfigGenerators.given
import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import pureconfig.*

final class DatabaseConfigSuite extends FunSuite:
  test("DatabaseConfig when loading invalid config format must fail"):
    val config = ConfigFactory.parseString("{}")
    ConfigSource.fromConfig(config).at("database").load[DatabaseConfig] match
      case Left(_)  => assert(true)
      case Right(_) => fail("Loading an invalid config must fail!")

final class DatabaseConfigGenSuite extends ScalaCheckSuite:
  property("DatabaseConfig loading valid config format when settings are invalid must fail"):
    forAll: (i: Int) =>
      val config = ConfigFactory.parseString(
        """database {
                 |  "driver":"",
                 |  "url":"",
                 |  "user": "",
                 |  "pass": ""
                 |}""".stripMargin
      )
      ConfigSource.fromConfig(config).at("database").load[DatabaseConfig] match
        case Left(_)  => assert(true)
        case Right(_) => fail("Loading a config with invalid settings must fail!")

  property("DatabaseConfig settings are valid must load correct settings"):
    forAll: (expected: DatabaseConfig) =>
      val config = ConfigFactory.parseString(
        s"""database {
                  |  "driver": "${expected.driver}",
                  |  "url": "${expected.url}",
                  |  "user": "${expected.user}",
                  |  "pass": "${expected.pass}"
                  |}""".stripMargin
      )
      ConfigSource.fromConfig(config).at("database").load[DatabaseConfig] match
        case Left(e)  => fail(s"Parsing a valid configuration must succeed! ($e)")
        case Right(c) => assertEquals(c, expected)

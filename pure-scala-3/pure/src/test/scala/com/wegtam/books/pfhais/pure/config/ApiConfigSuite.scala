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

package com.wegtam.books.pfhais.pure.config

import com.typesafe.config.*
import com.wegtam.books.pfhais.pure.config.ApiConfigGenerators.{ *, given }
import munit.FunSuite
import munit.ScalaCheckSuite
import org.scalacheck.Prop.*
import pureconfig.*

class ApiConfigSuite extends FunSuite:
  test("ApiConfig loading invalid config format must fail"):
    val config = ConfigFactory.parseString("{}")
    ConfigSource.fromConfig(config).at("api").load[ApiConfig] match
      case Left(_)  => assert(true)
      case Right(_) => fail("Loading an empty config must fail!")

class ApiConfigGenSuite extends ScalaCheckSuite:
  property("ApiConfig loading valid config format when settings are invalid host must fail"):
    forAll(validPort): (i: Int) =>
      val config = ConfigFactory.parseString(s"""api{"host":"","port":$i}""")
      ConfigSource.fromConfig(config).at("api").load[ApiConfig] match
        case Left(_)  => assert(true)
        case Right(_) => fail("Loading a config with invalid host must fail!")

  property("ApiConfig loading valid config format when settings are invalid port must fail"):
    forAll(invalidPort): (i: Int) =>
      val config = ConfigFactory.parseString(s"""api{"host":"localhost","port":$i}""")
      ConfigSource.fromConfig(config).at("api").load[ApiConfig] match
        case Left(_)  => assert(true)
        case Right(_) => fail("Loading a config with invalid port must fail!")

  property("ApiConfig when settings are valid must load correct settings"):
    forAll: (expected: ApiConfig) =>
      val config =
        ConfigFactory.parseString(s"""api{"host":"${expected.host}","port":${expected.port}}""")
      ConfigSource.fromConfig(config).at("api").load[ApiConfig] match
        case Left(e)  => fail(s"Parsing a valid configuration must succeed! ($e)")
        case Right(c) => assertEquals(c, expected)

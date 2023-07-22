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

import com.wegtam.books.pfhais.pure.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import org.scalacheck.*
import com.wegtam.books.pfhais.pure.config.NonEmptyString

object DatabaseConfigGenerators:
  private val DefaultPassword: NonEmptyString = "secret"
  private val validPassword: Gen[String]      = Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString)
  private val genDatabaseConfig: Gen[DatabaseConfig] =
    for gp <- validPassword
    yield
      val password: Option[NonEmptyString] = gp.refineOption
      DatabaseConfig(
        driver = "org.postgresql.Driver",
        url = "jdbc:postgresql://localhost:5422/test-database",
        user = "pure",
        pass = password.getOrElse(DefaultPassword)
      )

  given Arbitrary[DatabaseConfig] = Arbitrary(genDatabaseConfig)

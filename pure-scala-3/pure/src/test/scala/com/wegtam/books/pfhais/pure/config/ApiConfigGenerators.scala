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

import org.scalacheck.*
import com.wegtam.books.pfhais.pure.{ *, given }
import io.github.iltotore.iron.{ *, given }
import io.github.iltotore.iron.constraint.all.*

object ApiConfigGenerators:
  private val DefaultHost: NonEmptyString = "api.example.com"
  private val DefaultPort: PortNumber     = 1234

  val validHost: Gen[String] = Gen.nonEmptyListOf(Gen.alphaNumChar).map(_.mkString)
  val validPort: Gen[Int]    = Gen.choose(1, 65535)
  val invalidPort: Gen[Int]  = Gen.oneOf(Gen.negNum[Int], Gen.const(0), Gen.choose(65536, 1000000))

  val genApiConfig: Gen[ApiConfig] = for {
    gh <- validHost
    gp <- validPort
  } yield {
    val host: Option[NonEmptyString] = gh.refineOption
    val port: Option[PortNumber]     = gp.refineOption
    ApiConfig(host = host.getOrElse(DefaultHost), port = port.getOrElse(DefaultPort))
  }

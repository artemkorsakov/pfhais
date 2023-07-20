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

object ApiConfigGenerators:
  val DefaultHost: String = "api.example.com"
  val DefaultPort: Int    = 1234

  val validPort: Gen[Int]   = Gen.choose(1, 65535)
  val invalidPort: Gen[Int] = Gen.oneOf(Gen.negNum[Int], Gen.choose(65536, 1000000))

  // val genApiConfig: Gen[ApiConfig] = for {
  //   gh <- Gen.nonEmptyListOf(Gen.alphaNumChar)
  //   gp <- Gen.choose(1, 65535)
  // } yield ApiConfig(host = gh.mkString, port = gp)

  // given Arbitrary[ApiConfig] = Arbitrary(genApiConfig)

//  val DefaultHost: NonEmptyString = "api.example.com"
//  val DefaultPort: PortNumber     = 1234
//
//  val genApiConfig: Gen[ApiConfig] = for {
//    gh <- Gen.nonEmptyListOf(Gen.alphaNumChar)
//    gp <- Gen.choose(1, 65535)
//    h = RefType.applyRef[NonEmptyString](gh.mkString).getOrElse(DefaultHost)
//    p = RefType.applyRef[PortNumber](gp).getOrElse(DefaultPort)
//  } yield ApiConfig(host = h, port = p)
//
//  implicit val arbitraryApiConfig: Arbitrary[ApiConfig] = Arbitrary(genApiConfig)

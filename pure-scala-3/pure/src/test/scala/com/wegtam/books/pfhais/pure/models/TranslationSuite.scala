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

package com.wegtam.books.pfhais.pure.models

import cats.data.*
import com.wegtam.books.pfhais.pure.models.TypeGenerators.given
import io.circe.*
import io.circe.parser.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import munit.ScalaCheckSuite
import org.scalacheck.Prop.*

class TranslationSuite extends ScalaCheckSuite:
  property("Translation when decoding from JSON when JSON format is invalid must return an error"):
    forAll: (s: String) =>
      assert(decode[Translation](s).isLeft)

  property(
    "Translation when decoding from JSON when JSON format is valid when data is invalid must return an error"
  ):
    forAll: (lc: LanguageCode) =>
      val json = s"""{"lang":"invalid$lc","name":""}"""
      decodeAccumulating[Translation](json) match
        case Validated.Invalid(errors) =>
          assertEquals(errors.length, 2)
          assertEquals(
            errors.head.getMessage(),
            "DecodingFailure at .lang: Should match ^[a-z]{2}$"
          )
          assertEquals(
            errors.last.getMessage(),
            "DecodingFailure at .name: !(Should only contain whitespaces)"
          )
        case _ => fail("Should return an error")

  property(
    "Translation when decoding from JSON when JSON format is valid when data is valid must return the correct types"
  ):
    forAll: (t: Translation) =>
      val json = s"""{
                |"lang": ${t.lang.asJson.noSpaces},
                |"name": ${t.name.asJson.noSpaces}
                |}""".stripMargin
      decode[Translation](json) match
        case Left(e)  => fail(e.getMessage)
        case Right(v) => assertEquals(v, t)

  property("Translation when encoding to JSON must return correct JSON"):
    forAll: (t: Translation) =>
      val json = t.asJson.noSpaces
      val expectedJson =
        s"""{"lang":${t.lang.asJson.noSpaces},"name":${t.name.asJson.noSpaces}}""".stripMargin
      assertEquals(json, expectedJson)

  property("Translation when encoding to JSON must return decodeable JSON"):
    forAll: (t: Translation) =>
      decode[Translation](t.asJson.noSpaces) match
        case Left(_)  => fail("Must be able to decode encoded JSON!")
        case Right(d) => assertEquals(d, t)

end TranslationSuite

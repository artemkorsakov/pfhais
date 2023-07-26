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
import cats.implicits.*
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

class ProductTest extends ScalaCheckSuite:
  property("Product when decoding from JSON when JSON format is invalid must return an error"):
    forAll: (s: String) =>
      assert(decode[Product](s).isLeft)

  property(
    "Product when decoding from JSON when JSON format is valid when data is invalid must return an error"
  ):
    forAll: (id: String, names: List[String]) =>
      val json = s"""{"id":${id.asJson.noSpaces},"names":${names.asJson.noSpaces}}"""
      decodeAccumulating[Product](json) match
        case Validated.Invalid(errors) =>
          assertEquals(
            errors.head.getMessage(),
            "DecodingFailure at .id: Should match ^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
          )
        case _ => fail("Should return an error")

  property(
    "Product when decoding from JSON when JSON format is valid when data is valid must return the correct types"
  ):
    forAll: (p: Product) =>
      val json = s"""{"id":${p.id.asJson.noSpaces},"names":${p.names.asJson.noSpaces}}"""
      decode[Product](json) match
        case Left(e)  => fail(e.getMessage)
        case Right(v) => assertEquals(v, p)

  property("Product when encoding to JSON must return correct JSON"):
    forAll: (p: Product) =>
      val json         = p.asJson.noSpaces
      val expectedJson = s"""{"id":${p.id.asJson.noSpaces},"names":${p.names.asJson.noSpaces}}"""
      assertEquals(json, expectedJson)

  property("Product when encoding to JSON must return decodeable JSON"):
    forAll: (p: Product) =>
      decode[Product](p.asJson.noSpaces) match
        case Left(_)  => fail("Must be able to decode encoded JSON!")
        case Right(d) => assertEquals(d, p)

  property("Product when #fromDatabase must create correct results"):
    forAll: (p: Product) =>
      val rows = p.names.toNonEmptyList.map(t => (p.id, t.lang, t.name)).toList
      assert(Product.fromDatabase(rows).contains(p))

  property("Product when ordering must sort by ID"):
    forAll: (ps: List[Product]) =>
      val expected = ps.map(_.id).sorted
      val sorted   = ps.sorted.map(_.id)
      assertEquals(sorted, expected)

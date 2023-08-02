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

package com.wegtam.books.pfhais.models

import java.util.UUID

import cats.data.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import org.scalacheck.*

object TypeGenerators:

  private val genLanguageCode: Gen[LanguageCode] = Gen.oneOf(LanguageCodes.all)
  given Arbitrary[LanguageCode]                  = Arbitrary(genLanguageCode)

  private val DefaultProductName: ProductName = "I am a product name!"
  private val genProductName: Gen[ProductName] =
    for cs <- Gen.nonEmptyListOf(Gen.alphaNumChar)
    yield
      val name: Option[ProductName] = cs.mkString.refineOption
      name.getOrElse(DefaultProductName)
  private val genTranslation: Gen[Translation] =
    for
      c <- genLanguageCode
      n <- genProductName
    yield Translation(lang = c, name = n)
  given Arbitrary[Translation] = Arbitrary(genTranslation)

  private def getProductId: ProductId      = UUID.randomUUID.toString.refine
  private val genProductId: Gen[ProductId] = Gen.delay(getProductId)
  given Arbitrary[ProductId]               = Arbitrary(genProductId)

  private val genTranslationList: Gen[List[Translation]] =
    for ts <- Gen.nonEmptyListOf(genTranslation) yield ts
  private val genNonEmptyTranslationSet: Gen[NonEmptySet[Translation]] =
    for
      t  <- genTranslation
      ts <- genTranslationList
    yield NonEmptySet.of(t, ts*)
  private val genProduct: Gen[Product] =
    for
      id <- genProductId
      ts <- genNonEmptyTranslationSet
    yield Product(id = id, names = ts)
  given Arbitrary[Product] = Arbitrary(genProduct)

  private val genProducts: Gen[List[Product]] = Gen.nonEmptyListOf(genProduct)
  given Arbitrary[List[Product]]              = Arbitrary(genProducts)

end TypeGenerators

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

package com.wegtam.books.pfhais.impure.models

import cats.*
import cats.syntax.order.*
import io.circe.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.*

/** The translation data for a product name.
  *
  * @param lang
  *   A language code specifying the target translation.
  * @param name
  *   The product name in the language.
  */
final case class Translation(lang: LanguageCode, name: ProductName)

object Translation:

  /** Try to create an instance of a Translation from unsafe input data.
    *
    * @param lang
    *   A string that must contain a valid LanguageCode.
    * @param name
    *   A string that must contain a valid ProductName.
    * @return
    *   An option to the successfully created Translation.
    */
  def fromUnsafe(lang: String)(name: String): Option[Translation] =
    val maybeLC: Option[LanguageCode] = lang.refineOption
    val maybePN: Option[ProductName]  = name.refineOption
    for
      l <- maybeLC
      n <- maybePN
    yield Translation(lang = l, name = n)

  given Decoder[Translation] = Decoder.forProduct2("lang", "name")(Translation.apply)

  given Encoder[Translation] = Encoder.forProduct2("lang", "name")(t => (t.lang, t.name))

  given Order[Translation] = (x: Translation, y: Translation) => x.lang.compare(y.lang)

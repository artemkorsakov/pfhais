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

import cats.Order
import cats.data.NonEmptySet
import cats.implicits.*
import scala.collection.immutable.SortedSet

/** A product.
  *
  * @param id
  *   The unique ID of the product.
  * @param names
  *   A list of translations of the product name.
  */
final case class Product(id: ProductId, names: NonEmptySet[Translation])

object Product:
  given Order[Product] with
    def compare(x: Product, y: Product): Int = x.id.compare(y.id)

  /** Try to create a Product from the given list of database rows.
    *
    * @param rows
    *   The database rows describing a product and its translations.
    * @return
    *   An option to the successfully created Product.
    */
  def fromDatabase(rows: Seq[(ProductId, LanguageCode, ProductName)]): Option[Product] =
    val translations = rows.map { case (_, c, n) => Translation(lang = c, name = n) }
    for
      (id, _, _) <- rows.headOption
      names      <- NonEmptySet.fromSet(SortedSet.from(translations))
    yield Product(id = id, names = names)

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

package com.wegtam.books.pfhais.impure

import cats.*
import cats.syntax.order.*
import io.github.iltotore.iron.constraint.all.*
import io.github.iltotore.iron.*

import java.util.UUID

package object models {

  // A language code format according to ISO 639-1. Please note that this only verifies the format!
  type LanguageCode = String :| Match["^[a-z]{2}$"]
  // A product id which must be a valid UUID in version 4.
  type ProductId = UUID
  // A product name must be a non-empty string.
  type ProductName = String :| Not[Empty]

  given Order[LanguageCode] = (x: LanguageCode, y: LanguageCode) => x.compare(y)

  given Order[ProductName] = (x: ProductName, y: ProductName) => x.compare(y)

}

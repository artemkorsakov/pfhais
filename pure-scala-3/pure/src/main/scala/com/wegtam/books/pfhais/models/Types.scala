package com.wegtam.books.pfhais.models

import cats.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

// A language code format according to ISO 639-1. Please note that this only verifies the format!
type LanguageCode = String :| Match["^[a-z]{2}$"]
// A product id which must be a valid UUID in version 4.
type ProductId = String :| Match["^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"]
// A product name must be a non-empty string.
type ProductName = String :| Not[Blank]

given Order[LanguageCode] with
  def compare(x: LanguageCode, y: LanguageCode): Int = x.compare(y)

given Order[ProductId] with
  def compare(x: ProductId, y: ProductId): Int = x.compare(y)

given Order[ProductName] with
  def compare(x: ProductName, y: ProductName): Int = x.compare(y)

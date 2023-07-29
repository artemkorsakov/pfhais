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

package com.wegtam.books.pfhais.db

import cats.effect.*
import cats.implicits.*
import com.wegtam.books.pfhais.models.*
import fs2.Stream

class TestRepository[F[_]: Concurrent](source: Ref[F, Seq[Product]]) extends Repository[F]:
  override def loadProduct(id: ProductId): F[Seq[(ProductId, LanguageCode, ProductName)]] =
    for data <- source.get
    yield for
      product <- data.find(_.id == id).toSeq
      name    <- product.names.toIterable
    yield (product.id, name.lang, name.name)

  override def loadProducts(): Stream[F, (ProductId, LanguageCode, ProductName)] =
    val rows =
      for data <- source.get
      yield data.flatMap(p => p.names.toIterable.map(n => (p.id, n.lang, n.name)))
    Stream.evalSeq(rows)

  override def saveProduct(product: Product): F[Int] =
    for
      data <- source.get
      found   = data.exists(_.id == product.id)
      newData = if found then data else data :+ product
      _ <- source.set(newData)
    yield if found then 0 else 1

  override def updateProduct(product: Product): F[Int] =
    for
      data <- source.get
      found   = data.exists(_.id == product.id)
      newData = if found then data.filterNot(_.id == product.id) :+ product else data
      _ <- source.set(newData)
    yield if found then 1 else 0

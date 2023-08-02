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

package com.wegtam.books.pfhais.api

import cats.*
import cats.effect.*
import cats.implicits.*
import com.wegtam.books.pfhais.db.*
import com.wegtam.books.pfhais.models.*
import fs2.Stream
import io.circe.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.cats.given
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import org.http4s.*
import org.http4s.circe.*
import org.http4s.dsl.*

final class ProductsRoutes[F[_]: Concurrent](repo: Repository[F]) extends Http4sDsl[F]:
  given EntityDecoder[F, Product] = jsonOf

  val routes: HttpRoutes[F] = HttpRoutes.of[F]:
    case GET -> Root / "products" =>
      val prefix = Stream.eval("[".pure[F])
      val suffix = Stream.eval("]".pure[F])
      val ps = repo
        .loadProducts()
        .groupAdjacentBy(_._1)
        .map: (id, rows) =>
          Product.fromDatabase(rows.toList)
        .collect { case Some(p) => p }
        .map(_.asJson.noSpaces)
        .intersperse(",")
      @SuppressWarnings(Array("org.wartremover.warts.Any"))
      val result: Stream[F, String] = prefix ++ ps ++ suffix
      Ok(result)
    case req @ POST -> Root / "products" =>
      req
        .as[Product]
        .flatMap: p =>
          for
            cnt <- repo.saveProduct(p)
            res <- cnt match
              case 0 => InternalServerError()
              case _ => NoContent()
          yield res
        .handleErrorWith { case _: InvalidMessageBodyFailure =>
          BadRequest()
        }

end ProductsRoutes

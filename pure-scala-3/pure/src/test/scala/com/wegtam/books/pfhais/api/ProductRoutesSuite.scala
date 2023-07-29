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

import java.util.UUID
import cats.*
import cats.data.NonEmptySet
import cats.effect.*
import com.wegtam.books.pfhais.db.*
import com.wegtam.books.pfhais.models.*
import io.circe.*
import io.circe.syntax.*
import io.circe.generic.auto.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.circe.given
import io.github.iltotore.iron.constraint.all.*
import org.http4s.{ EntityDecoder, HttpRoutes, Method, Request, Status, Uri }
import org.http4s.implicits.*
import org.http4s.circe.jsonOf
import org.http4s.server.Router
import munit.CatsEffectSuite

final class ProductRoutesSuite extends CatsEffectSuite:
  given EntityDecoder[IO, Product] = jsonOf

  private val productId: ProductId  = UUID.randomUUID.toString.refine
  private val names                 = NonEmptySet.one(Translation(lang = "ru", name = "имя"))
  private val otherNames            = NonEmptySet.one(Translation(lang = "en", name = "name"))
  private val product: Product      = Product(productId, names)
  private val otherProduct: Product = Product(productId, otherNames)
  private val uri = Uri
    .fromString(s"/product/$productId")
    .getOrElse(fail("Could not generate valid URI!"))
  private val getRequest = Request[IO](method = Method.GET, uri = uri)
  private val putRequest = Request[IO](method = Method.PUT, uri = uri)

  test(
    "ProductRoutes when GET /product/ID when product does not exist must return Status.NotFound"
  ):
    for
      service  <- createService(Seq.empty[Product])
      response <- service.orNotFound.run(getRequest)
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.NotFound)
      assert(body.isEmpty)

  test("ProductRoutes when GET /product/ID when product exists must return Status.Ok"):
    for
      service  <- createService(Seq(product))
      response <- service.orNotFound.run(getRequest)
      actual   <- response.as[Product]
    yield
      assertEquals(response.status, Status.Ok)
      assertEquals(actual, product)

  test(
    "ProductRoutes when PUT /product/ID when request body is invalid must return Status.BadRequest"
  ):
    for
      service <- createService(Seq.empty[Product])
      payload = scala.util.Random.alphanumeric.take(256).mkString
      response <- service.orNotFound.run(putRequest.withEntity(payload.asJson.noSpaces))
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.BadRequest)
      assert(body.isEmpty)

  test(
    "ProductRoutes when PUT /product/ID when request body is valid when product does not exist must return Status.NotFound"
  ):
    for
      service  <- createService(Seq.empty[Product])
      response <- service.orNotFound.run(putRequest.withEntity(product.asJson.noSpaces))
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.NotFound)
      assert(body.isEmpty)

  test(
    "ProductRoutes when PUT /product/ID when request body is valid when product exists must return Status.NoContent"
  ):
    for
      service  <- createService(Seq(product))
      response <- service.orNotFound.run(putRequest.withEntity(product.asJson.noSpaces))
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.NoContent)
      assert(body.isEmpty)

  test(
    "ProductRoutes when GET /product/ID after PUT /product/ID must return an updated product"
  ):
    for
      service            <- createService(Seq(otherProduct))
      oldProductResponse <- service.orNotFound.run(getRequest)
      oldProduct         <- oldProductResponse.as[Product]
      _                  <- service.orNotFound.run(putRequest.withEntity(product.asJson.noSpaces))
      updatedProductResponse <- service.orNotFound.run(getRequest)
      updatedProduct         <- updatedProductResponse.as[Product]
    yield
      assertEquals(oldProduct, otherProduct)
      assertEquals(updatedProduct, product)

  private def createService(products: Seq[Product]): IO[HttpRoutes[IO]] =
    for ref <- Ref[IO].of(products)
    yield
      val repository = new TestRepository[IO](ref)
      Router("/" -> new ProductRoutes(repository).routes)

end ProductRoutesSuite

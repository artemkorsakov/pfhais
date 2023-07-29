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

final class ProductsRoutesSuite extends CatsEffectSuite:
  given EntityDecoder[IO, Product]       = jsonOf
  given EntityDecoder[IO, List[Product]] = jsonOf

  private val firstProductId: ProductId = UUID.randomUUID.toString.refine
  private val firstNames = NonEmptySet.of(
    Translation(lang = "ru", name = "машина"),
    Translation(lang = "en", name = "a car")
  )
  private val firstProduct: Product = Product(firstProductId, firstNames)

  private val secondProductId: ProductId = UUID.randomUUID.toString.refine
  private val secondNames = NonEmptySet.of(
    Translation(lang = "ru", name = "ручка"),
    Translation(lang = "en", name = "a pen")
  )
  private val secondProduct: Product  = Product(secondProductId, secondNames)
  private val products: List[Product] = List(firstProduct, secondProduct)

  private val uri: Uri    = Uri.unsafeFromString("/products")
  private val getRequest  = Request[IO](method = Method.GET, uri = uri)
  private val postRequest = Request[IO](method = Method.POST, uri = uri)

  test(
    "ProductsRoutes when GET /products when no products exist must return Status.Ok and an empty list"
  ):
    for
      service  <- createService(Seq.empty[Product])
      response <- service.orNotFound.run(getRequest)
      products <- response.as[List[Product]]
    yield
      assertEquals(response.status, Status.Ok)
      assert(products.isEmpty)

  test(
    "ProductsRoutes when GET /products when products exist must return Status.Ok and and a list of products"
  ):
    for
      service        <- createService(products)
      response       <- service.orNotFound.run(getRequest)
      actualProducts <- response.as[List[Product]]
    yield
      assertEquals(response.status, Status.Ok)
      assertEquals(actualProducts, products)

  test(
    "ProductsRoutes when POST /products when request body is invalid must return Status.BadRequest"
  ):
    for
      service <- createService(Seq.empty[Product])
      payload = scala.util.Random.alphanumeric.take(256).mkString
      response <- service.orNotFound.run(postRequest.withEntity(payload.asJson.noSpaces))
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.BadRequest)
      assert(body.isEmpty)

  test(
    "ProductsRoutes when POST /products when request body is valid when product could be saved must return Status.NoContent"
  ):
    for
      service  <- createService(Seq(firstProduct))
      response <- service.orNotFound.run(postRequest.withEntity(secondProduct.asJson.noSpaces))
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.NoContent)
      assert(body.isEmpty)

  test(
    "ProductsRoutes when POST /products when request body is valid when product could not be saved must return Status.InternalServerError"
  ):
    for
      service  <- createService(Seq(firstProduct))
      response <- service.orNotFound.run(postRequest.withEntity(firstProduct.asJson.noSpaces))
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.InternalServerError)
      assert(body.isEmpty)

  test(
    "ProductsRoutes when GET /products after POST /products must return saved products"
  ):
    for
      service        <- createService(Seq(firstProduct))
      _              <- service.orNotFound.run(postRequest.withEntity(secondProduct.asJson.noSpaces))
      response       <- service.orNotFound.run(getRequest)
      actualProducts <- response.as[List[Product]]
    yield assertEquals(actualProducts, products)

  private def createService(products: Seq[Product]): IO[HttpRoutes[IO]] =
    for ref <- Ref[IO].of(products)
    yield
      val repository = new TestRepository[IO](ref)
      Router("/" -> new ProductsRoutes(repository).routes)

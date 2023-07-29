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
import cats.effect.*
import com.wegtam.books.pfhais.db.*
import com.wegtam.books.pfhais.models.*
import org.http4s.{HttpRoutes, Method, Request, Status, Uri}
import org.http4s.implicits.*
import org.http4s.server.Router
import munit.CatsEffectSuite

final class ProductRoutesSuite extends CatsEffectSuite:
  test(
    "ProductRoutes when GET /product/ID when product does not exist must return Status.NotFound"
  ):
    for
      ref <- Ref[IO].of(Seq.empty[Product])
      repository = new TestRepository[IO](ref)
      id         = UUID.randomUUID.toString
      uri = Uri
        .fromString(s"/product/$id")
        .getOrElse(fail("Could not generate valid URI!"))
      service = Router("/" -> new ProductRoutes(repository).routes)
      response <- service.orNotFound.run(Request(method = Method.GET, uri = uri))
      body     <- response.body.compile.toVector
    yield
      assertEquals(response.status, Status.NotFound)
      assert(body.isEmpty)

    //       val result = response.unsafeRunSync()
    //       result.status must be(expectedStatusCode)
    //       result.body.compile.toVector.unsafeRunSync must be(empty)
    //   }

//       "product exists" must {
//         val expectedStatusCode = Status.Ok

//         s"return $expectedStatusCode and the product" in {
//           forAll("product") {
//             p: Product =>
//               Uri.fromString("/product/" + p.id.toString) match {
//                 case Left(_) => fail("Could not generate valid URI!")
//                 case Right(u) =>
//                   val repo: Repository[IO]    = new TestRepository[IO](Seq(p))
//                   def service: HttpRoutes[IO] = Router("/" -> new ProductRoutes(repo).routes)
//                   val response: IO[Response[IO]] = service.orNotFound.run(
//                     Request(method = Method.GET, uri = u)
//                   )
//                   val result = response.unsafeRunSync
//                   result.status must be(expectedStatusCode)
//                   result.as[Product].unsafeRunSync must be(p)
//               }
//           }
//         }
//       }
//     }

//     "PUT /product/ID" when {
//       "request body is invalid" must {
//         val expectedStatusCode = Status.BadRequest

//         s"return $expectedStatusCode" in {
//           forAll("id") {
//             id: ProductId =>
//               Uri.fromString("/product/" + id.toString) match {
//                 case Left(_) => fail("Could not generate valid URI!")
//                 case Right(u) =>
//                   def service: HttpRoutes[IO] =
//                     Router("/" -> new ProductRoutes(emptyRepository).routes)
//                   val payload = scala.util.Random.alphanumeric.take(256).mkString
//                   val response: IO[Response[IO]] = service.orNotFound.run(
//                     Request(method = Method.PUT, uri = u)
//                       .withEntity(payload.asJson.noSpaces)
//                   )
//                   val result = response.unsafeRunSync
//                   result.status must be(expectedStatusCode)
//                   result.body.compile.toVector.unsafeRunSync must be(empty)
//               }
//           }
//         }
//       }

//       "request body is valid" when {
//         "product does not exist" must {
//           val expectedStatusCode = Status.NotFound

//           s"return $expectedStatusCode" in {
//             forAll("product") {
//               p: Product =>
//                 Uri.fromString("/product/" + p.id.toString) match {
//                   case Left(_) => fail("Could not generate valid URI!")
//                   case Right(u) =>
//                     def service: HttpRoutes[IO] =
//                       Router("/" -> new ProductRoutes(emptyRepository).routes)
//                     val response: IO[Response[IO]] = service.orNotFound.run(
//                       Request(method = Method.PUT, uri = u)
//                         .withEntity(p)
//                     )
//                     val result = response.unsafeRunSync
//                     result.status must be(expectedStatusCode)
//                     result.body.compile.toVector.unsafeRunSync must be(empty)
//                 }
//             }
//           }
//         }

//         "product exists" must {
//           val expectedStatusCode = Status.NoContent

//           s"return $expectedStatusCode" in {
//             forAll("product") {
//               p: Product =>
//                 Uri.fromString("/product/" + p.id.toString) match {
//                   case Left(_) => fail("Could not generate valid URI!")
//                   case Right(u) =>
//                     val repo: Repository[IO]    = new TestRepository[IO](Seq(p))
//                     def service: HttpRoutes[IO] = Router("/" -> new ProductRoutes(repo).routes)
//                     val response: IO[Response[IO]] = service.orNotFound.run(
//                       Request(method = Method.PUT, uri = u)
//                         .withEntity(p)
//                     )
//                     val result = response.unsafeRunSync
//                     result.status must be(expectedStatusCode)
//                     result.body.compile.toVector.unsafeRunSync must be(empty)
//                 }
//             }
//           }
//         }
//       }
//     }
//   }
// }

end ProductRoutesSuite

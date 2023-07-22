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

package com.wegtam.books.pfhais.pure.config

import com.wegtam.books.pfhais.pure.*
import pureconfig.ConfigReader
import pureconfig.generic.derivation.default.*
import com.wegtam.books.pfhais.pure.config.{ DatabaseUrl, NonEmptyString }

/** The configuration for our database connection.
  *
  * @param driver
  *   The class name of the driver to use.
  * @param url
  *   The JDBC connection url (driver specific).
  * @param user
  *   The username for the database connection.
  * @param pass
  *   The password for the database connection.
  */
final case class DatabaseConfig(
    driver: NonEmptyString,
    url: DatabaseUrl,
    user: NonEmptyString,
    pass: NonEmptyString
) derives ConfigReader

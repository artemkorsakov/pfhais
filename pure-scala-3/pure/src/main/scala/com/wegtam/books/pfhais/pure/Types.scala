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

package com.wegtam.books.pfhais.pure

import pureconfig.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

// A string containing a database login which must be non empty.
opaque type DatabaseLogin = String :| Not[Blank]
// A string containing a database password which must be non empty.
opaque type DatabasePassword = String :| Not[Blank]
// A string containing a database url.
opaque type DatabaseUrl = String :|
  Match["""(\b(https?|ftp|file)://)?[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]"""]
// A string that must not be empty.
opaque type NonEmptyString = String :| Not[Blank]
// A TCP port number which is valid in the range of 1 to 65535.
opaque type PortNumber = Int :| Interval.Closed[1, 65535]

given Constraint[String, NonEmptyString] with
  override inline def test(value: String): Boolean = value.refineOption.isDefined
  override inline def message: String              = "Should be non empty"

given ConfigReader[NonEmptyString] =
  ConfigReader.fromString[NonEmptyString](ConvertHelpers.catchReadError(_.refine))

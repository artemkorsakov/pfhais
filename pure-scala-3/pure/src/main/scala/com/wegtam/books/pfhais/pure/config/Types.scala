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

import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*
import pureconfig.*

type NonEmptyString = String :| Not[Blank]

type DatabaseUrl = String :|
  Match["""(\b(https?|ftp|file)://)?[-A-Za-z0-9+&@#/%?=~_|!:,.;]+[-A-Za-z0-9+&@#/%=~_|]"""]

// A TCP port number which is valid in the range of 1 to 65535.
type PortNumber = Int :| Interval.Closed[1, 65535]

given ConfigReader[NonEmptyString] =
  ConfigReader.fromString[NonEmptyString](ConvertHelpers.optF(_.refineOption))
given ConfigReader[DatabaseUrl] =
  ConfigReader.fromString[DatabaseUrl](ConvertHelpers.optF(_.refineOption))
given ConfigReader[PortNumber] =
  ConfigReader.fromString[PortNumber](ConvertHelpers.optF(_.toIntOption.flatMap(_.refineOption)))

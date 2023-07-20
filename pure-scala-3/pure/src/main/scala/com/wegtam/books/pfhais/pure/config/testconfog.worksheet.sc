import pureconfig.*
import pureconfig.generic.derivation.default.*
import com.typesafe.config.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

opaque type NonEmptyString = String :| Not[Blank]
opaque type PortNumber     = Int :| Interval.Closed[1, 65535]

given ConfigReader[NonEmptyString] =
  ConfigReader.fromString[NonEmptyString](ConvertHelpers.optF(_.refineOption))

given ConfigReader[PortNumber] =
  ConfigReader.fromString[PortNumber](ConvertHelpers.optF(_.toIntOption.flatMap(_.refineOption)))

final case class ApiConfig(host: NonEmptyString, port: PortNumber) derives ConfigReader

val config1 = ConfigFactory.parseString("""api{"host":"localhost","port":1234}""")
ConfigSource.fromConfig(config1).at("api").load[ApiConfig]

val config2 = ConfigFactory.parseString("""api{"host":"","port":-1}""")
ConfigSource.fromConfig(config2).at("api").load[ApiConfig]

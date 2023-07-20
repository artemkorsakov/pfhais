import pureconfig.*
import pureconfig.generic.derivation.default.*
import com.typesafe.config.*
import io.github.iltotore.iron.*
import io.github.iltotore.iron.constraint.all.*

opaque type NonEmptyString = String :| Not[Blank]
opaque type PortNumber = Int :| Interval.Closed[1, 65535]

final case class ApiConfig(host: NonEmptyString, port: Int) derives ConfigReader

object ApiConfig:
  given Constraint[String, NonEmptyString] with
    override inline def test(value: String): Boolean = value.refineOption.isDefined
    override inline def message: String = "Should be non empty"

  given ConfigReader[NonEmptyString] = ConfigReader.fromString[NonEmptyString](ConvertHelpers.catchReadError(_.refine))

val config1 = ConfigFactory.parseString("""api{"host":"localhost","port":1234}""")
ConfigSource.fromConfig(config1).at("api").load[ApiConfig]

val config2 = ConfigFactory.parseString("""api{"host":"","port":-1}""")
ConfigSource.fromConfig(config2).at("api").load[ApiConfig]

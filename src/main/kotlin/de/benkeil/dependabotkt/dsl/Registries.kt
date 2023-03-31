package de.benkeil.dependabotkt.dsl

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonValue
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.encodeStructure

@Serializable open class Registry(open val type: RegistryType, @JsonIgnore open val name: String)

class Serde : KSerializer<DefaultRepository> {
  override val descriptor: SerialDescriptor
    get() =
        buildClassSerialDescriptor("DefaultRepository") {
          element("type", String.serializer().descriptor)
          element("name", String.serializer().descriptor)
          element("url", String.serializer().descriptor)
          element("username", String.serializer().descriptor)
          element("password", String.serializer().descriptor)
          element("replacesBase", Boolean.serializer().descriptor)
        }

  override fun deserialize(decoder: Decoder): DefaultRepository {
    TODO("Not yet implemented")
  }

  override fun serialize(encoder: Encoder, value: DefaultRepository) =
      encoder.encodeStructure(descriptor) {
        encodeStringElement(descriptor, 0, value.type.toString())
        encodeStringElement(descriptor, 1, value.name)
        encodeStringElement(descriptor, 2, value.url)
        value.username?.let { encodeStringElement(descriptor, 3, it) }
        value.password?.let { encodeStringElement(descriptor, 4, it) }
        value.replacesBase?.let { encodeBooleanElement(descriptor, 5, it) }
      }
}

@Serializable(with = Serde::class)
class DefaultRepository(
    override val type: RegistryType,
    override val name: String,
    val url: String,
    val username: String? = null,
    val password: String? = null,
    val replacesBase: Boolean? = null
) : Registry(type, name)

@DependabotDslMarker
class RegistriesContext {
  private val registries: MutableList<Registry> = mutableListOf()

  fun registry(block: DefaultRegistryContext.() -> Unit) =
      DefaultRegistryContext().apply(block).run { this@RegistriesContext.registries.add(build()) }

  fun maven(block: DefaultRegistryContext.() -> Unit) = registry {
    type = RegistryType.MavenRepository
    block()
  }

  fun docker(block: DefaultRegistryContext.() -> Unit) = registry {
    type = RegistryType.DockerRegistry
    block()
  }

  fun gitHubMaven(block: GitHubMavenContext.() -> Unit) {
    GitHubMavenContext().apply(block).also {
      registry {
        type = RegistryType.MavenRepository
        name = it.slug
        replacesBase = it.replacesBase
        url = "https://maven.pkg.github.com/${it.slug}"
        username = it.username
        password = it.password
      }
    }
  }

  internal fun build() = registries.associateBy { it.name }
}

@DependabotDslMarker
class GitHubMavenContext {
  lateinit var slug: String
  var username: String = "dependabot"
  var password: String = expr { SecretsContext.DEPENDABOT_TOKEN }
  var replacesBase: Boolean? = null
}

@DependabotDslMarker
class DefaultRegistryContext {
  lateinit var type: RegistryType
  lateinit var name: String
  lateinit var url: String
  var username: String? = null
  var password: String? = null
  var replacesBase: Boolean? = null

  fun build(type: RegistryType? = null) =
      DefaultRepository(
          type = type ?: this.type,
          name = name,
          url = url,
          username = username,
          password = password,
          replacesBase = replacesBase,
      )
}

enum class RegistryType(private val value: String) {
  ComposerRepository("composer-repository"),
  DockerRegistry("docker-registry"),
  Git("git"),
  HexOrganization("hex-organization"),
  HexRepository("hex-repository"),
  MavenRepository("maven-repository"),
  NpmRegistry("npm-registry"),
  NugetFeed("nuget-feed"),
  PythonIndex("python-index"),
  RubygemsServer("rubygems-server"),
  TerraformRegistry("terraform-registry"),
  ;

  @JsonValue
  override fun toString(): String {
    return value
  }
}

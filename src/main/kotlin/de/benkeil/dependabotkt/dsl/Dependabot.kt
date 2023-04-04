package de.benkeil.dependabotkt.dsl

import de.benkeil.dependabotkt.extension.findGitRoot
import de.benkeil.dependabotkt.serialization.toYaml
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlinx.serialization.Serializable

@Serializable
data class Dependabot(
    val version: String,
    val registries: Map<String, Registry>? = null,
    val updates: Collection<Update>? = null,
)

fun Dependabot.toFile(override: Boolean, rootPath: Path = Path.of("")): File =
    rootPath
        .toAbsolutePath()
        .findGitRoot()
        .let { File(it.absolutePathString(), ".github/dependabot.yml") }
        .also { it.parentFile.mkdirs() }
        .also {
          when {
            it.exists() && override -> it.writeText(toYaml())
            it.exists() && !override ->
                println(
                    """
                  dependabot.yml already exists, if you want to override it set: 
                    
                  dependabot {
                    override = true
                    ...
                  }
                  """
                        .trimIndent())
            !it.exists() -> it.writeText(toYaml())
          }
        }

@DependabotDslMarker
open class DependabotContext {
  var version: String = "2"
  private var registries: Map<String, Registry>? = null
  private var updates: Collection<Update>? = null

  /**
   * The top-level `registries` key is optional. It allows you to specify authentication details that Dependabot can use to access
   * private package registries.
   */
  fun registries(block: RegistriesContext.() -> Unit) {
    registries = RegistriesContext().apply(block).build()
  }

  fun updates(block: UpdatesContext.() -> Unit) {
    updates = UpdatesContext(registries).apply(block).build()
  }

  fun build(): Dependabot = Dependabot(version, registries, updates)
}

fun dependabot(block: DependabotContext.() -> Unit) = DependabotContext().also(block).build()

package de.benkeil.dependabotkt.dsl

import kotlinx.serialization.Serializable

@Serializable
data class Dependabot(
    val version: String,
    val registries: Map<String, Registry>? = null,
    val updates: Collection<Update>? = null,
)

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

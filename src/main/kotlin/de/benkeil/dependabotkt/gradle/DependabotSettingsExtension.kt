package de.benkeil.dependabotkt.gradle

import de.benkeil.dependabotkt.dsl.DependabotContext
import java.io.Serializable
import org.gradle.api.initialization.Settings

open class DependabotSettingsExtension(val settings: Settings) : Serializable, DependabotContext() {
  companion object {
    internal const val NAME: String = "dependabot"
  }

  var override: Boolean = false
}

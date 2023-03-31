package de.benkeil.dependabotkt.gradle

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

abstract class DependabotPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {
    settings.extensions.create(DependabotSettingsExtension.NAME, DependabotSettingsExtension::class.java, settings)
  }
}

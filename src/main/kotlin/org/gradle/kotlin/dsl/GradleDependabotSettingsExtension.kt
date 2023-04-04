package org.gradle.kotlin.dsl

import de.benkeil.dependabotkt.dsl.toFile
import de.benkeil.dependabotkt.gradle.DependabotSettingsExtension
import org.gradle.api.initialization.Settings

inline fun Settings.dependabot(configure: DependabotSettingsExtension.() -> Unit) {
  extensions.getByType<DependabotSettingsExtension>().apply(configure).also { it.build().toFile(it.override, rootDir.toPath()) }
}

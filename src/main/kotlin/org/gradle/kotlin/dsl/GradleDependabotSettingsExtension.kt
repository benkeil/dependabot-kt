package org.gradle.kotlin.dsl

import de.benkeil.dependabotkt.dsl.Dependabot
import de.benkeil.dependabotkt.extension.findGitRoot
import de.benkeil.dependabotkt.gradle.DependabotSettingsExtension
import de.benkeil.dependabotkt.serialization.toYaml
import java.io.File
import java.nio.file.Path
import kotlin.io.path.absolutePathString
import kotlin.io.path.createDirectories
import org.gradle.api.initialization.Settings

inline fun Settings.dependabot(configure: DependabotSettingsExtension.() -> Unit) {
  extensions.getByType<DependabotSettingsExtension>().apply(configure).also { it.build().toFile(it.override, rootDir.toPath()) }
}

fun Dependabot.toFile(override: Boolean, rootPath: Path = Path.of("")): File =
    rootPath
        .toAbsolutePath()
        .findGitRoot()
        .let { File(it.absolutePathString(), ".github/dependabot.yml") }
        .also { it.toPath().parent.createDirectories() }
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

package de.benkeil.dependabotkt.gradle

import de.benkeil.dependabotkt.test.extension.deleteOnExit
import io.kotest.core.spec.style.FunSpec
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gradle.testkit.runner.GradleRunner

@OptIn(ExperimentalPathApi::class)
class DependabotPluginTest :
    FunSpec({
      test("configure plugin") {
        println(">> createTempDirectory")
        val projectDir = withContext(Dispatchers.IO) { Files.createTempDirectory("").deleteOnExit() }
        println(">> created")
        projectDir.resolve(".git").createDirectories()
        projectDir
            .resolve("settings.gradle.kts")
            .toFile()
            .writeText(
                """
        plugins {
          id("io.github.benkeil.dependabotkt")
        }

        dependabot {
          override = true
          registries {
            gitHubMaven { slug = "benkeil/library" }
          }
        }
      """
                    .trimIndent())

        val result =
            GradleRunner.create()
                .withProjectDir(projectDir.toFile())
                .withPluginClasspath()
                .withArguments("--info", "--stacktrace")
                .build()

        println(result.output)
        println(">> finished")
      }
    })

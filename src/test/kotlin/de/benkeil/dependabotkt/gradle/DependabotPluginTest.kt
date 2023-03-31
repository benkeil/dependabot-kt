package de.benkeil.dependabotkt.gradle

import io.kotest.core.spec.style.FunSpec
import java.nio.file.Files
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.createDirectories
import kotlin.io.path.deleteRecursively
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gradle.testkit.runner.GradleRunner

@OptIn(ExperimentalPathApi::class)
class DependabotPluginTest :
    FunSpec({
      test("configure plugin") {
        val projectDir = withContext(Dispatchers.IO) { Files.createTempDirectory("") }
        projectDir.resolve(".git").createDirectories()
        projectDir
            .resolve("settings.gradle.kts")
            .toFile()
            .writeText(
                """
        plugins {
          id("de.benkeil.dependabotkt")
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

        projectDir.deleteRecursively()
      }
    })

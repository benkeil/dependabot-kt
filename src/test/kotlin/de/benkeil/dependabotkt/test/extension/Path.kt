package de.benkeil.dependabotkt.test.extension

import java.nio.file.Path
import kotlin.concurrent.thread
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.deleteRecursively

fun Path.deleteOnExit() = DeleteOnExitHook.add(this).let { this }

object DeleteOnExitHook {
  private val paths = mutableSetOf<Path>()

  fun add(path: Path) = paths.add(path)

  @OptIn(ExperimentalPathApi::class)
  private val run = thread(start = false) { paths.reversed().asSequence().map { it.deleteRecursively() }.toList() }

  init {
    Runtime.getRuntime().addShutdownHook(run)
  }
}

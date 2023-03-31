package de.benkeil.dependabotkt.extensions

import java.nio.file.Path
import kotlin.io.path.absolute
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

internal fun Path.findGitRoot(): Path {
  return generateSequence(absolute()) { it.parent }.firstOrNull { it.resolve(".git").run { exists() && isDirectory() } }
      ?: error("could not find a git root from ${this.absolute()}")
}

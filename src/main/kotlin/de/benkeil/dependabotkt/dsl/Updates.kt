package de.benkeil.dependabotkt.dsl

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonValue
import de.benkeil.dependabotkt.serialization.kotlinx.LocalTimeKSerializer
import de.benkeil.dependabotkt.serialization.kotlinx.TimeZoneKSerializer
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.TimeZone
import java.util.TreeSet
import kotlinx.serialization.Serializable

// could be done with context receivers when stable
@DependabotDslMarker
class UpdatesContext(private val registries: Map<String, Registry>?) {
  private var updates = TreeSet<Update>(Comparator.comparing { it.packageEcosystem.name })
  fun update(block: UpdateContext.() -> Unit) {
    updates.add(UpdateContext(registries).apply(block).build())
  }

  internal fun build() = updates.toSet()
}

@DependabotDslMarker
class UpdateContext(private val usedRegistries: Map<String, Registry>?) {
  lateinit var packageEcosystem: PackageSystem
  lateinit var directory: String
  var schedule: Schedule? = null
  private var registries: Collection<String>? = null
  private var commitMessage: CommitMessageContext? = null
  private var ignore: Collection<DependencyContext>? = null
  var openPullRequestsLimit: Int? = null
  private var pullRequestBranchName: PullRequestBranchNameContext? = null

  fun registries(block: UpdateRegistriesContext.() -> Unit) {
    registries = UpdateRegistriesContext(usedRegistries).apply(block).build()
  }

  fun commitMessage(block: CommitMessageContext.() -> Unit) {
    commitMessage = CommitMessageContext().apply(block)
  }

  fun ignore(block: IgnoreContext.() -> Unit) {
    ignore = IgnoreContext().apply(block).build()
  }

  fun pullRequestBranchName(block: PullRequestBranchNameContext.() -> Unit) {
    pullRequestBranchName = PullRequestBranchNameContext().apply(block)
  }

  internal fun build() =
      Update(
          packageEcosystem = packageEcosystem,
          directory = directory,
          schedule = schedule,
          registries = registries,
          commitMessage = commitMessage,
          ignore = ignore,
          openPullRequestsLimit = openPullRequestsLimit)
}

@Serializable
data class Update(
    val packageEcosystem: PackageSystem,
    val directory: String,
    val schedule: Schedule? = null,
    val registries: Collection<String>? = null,
    val commitMessage: CommitMessageContext? = null,
    val ignore: Collection<DependencyContext>? = null,
    val openPullRequestsLimit: Int? = null,
    val pullRequestBranchName: PullRequestBranchNameContext? = null,
)

@Serializable
@DependabotDslMarker
class PullRequestBranchNameContext {
  lateinit var separator: String
}

@DependabotDslMarker
class IgnoreContext {
  private val ignores = mutableListOf<DependencyContext>()
  fun dependency(block: DependencyContext.() -> Unit) {
    ignores.add(DependencyContext().apply(block))
  }

  internal fun build() = ignores.toList()
}

@Serializable
@DependabotDslMarker
class DependencyContext {
  @JsonProperty("dependency-name") lateinit var name: String
  var versions: String? = null
  var type: UpdateType? = null

  @Serializable
  sealed class UpdateType(private val value: String) {
    object Version {
      object Semver {
        @Serializable object Major : UpdateType("version-update:semver-major")
        @Serializable object Minor : UpdateType("version-update:semver-minor")
        @Serializable object Patch : UpdateType("version-update:semver-patch")
      }
    }

    @JsonValue
    override fun toString(): String {
      return value
    }
  }
}

@Serializable
@DependabotDslMarker
class CommitMessageContext {
  var prefix: String? = null
  var prefixDevelopment: String? = null
  var include: String? = null
}

@Serializable
@DependabotDslMarker
class UpdateRegistriesContext(private val usedRegistries: Map<String, Registry>?) {
  private val registries = mutableListOf<String>()
  fun retrieve(name: String) {
    if (usedRegistries.isNullOrEmpty()) {
      throw RuntimeException("no configured registries in registries block")
    }
    if (!usedRegistries.containsKey(name)) {
      throw RuntimeException("registry $name not configured in registries block")
    }
    registries.add(name)
  }

  internal fun build() = registries.toList()
}

enum class PackageSystem(private val value: String) {
  Bundler("bundler"),
  Cargo("cargo"),
  Composer("composer"),
  Docker("	docker"),
  Hex("mix"),
  ElmPackage("elm"),
  Git("submodule"),
  GitHub("github"),
  GitHubActions("github-actions"),
  Go("modules"),
  Gradle("gradle"),
  Maven("maven"),
  Npm("npm"),
  NuGet("nuget"),
  Pip("pip"),
  Pipenv("pip"),
  PipCompile("pip"),
  Poetry("pip"),
  Pub("pub"),
  Terraform("terraform"),
  Yarn("npm"),
  ;

  @JsonValue
  override fun toString(): String {
    return value
  }
}

@Serializable
sealed class Schedule(val interval: String) {
  @Serializable
  class Daily(
      @field:JsonFormat(pattern = "KK:mm") @Serializable(with = LocalTimeKSerializer::class) val time: LocalTime? = null,
      @Serializable(with = TimeZoneKSerializer::class) val timezone: TimeZone = TimeZone.getDefault()
  ) : Schedule("daily")

  @Serializable
  class Weekly(
      @field:JsonFormat(pattern = "KK:mm") @Serializable(with = LocalTimeKSerializer::class) val time: LocalTime? = null,
      val day: DayOfWeek? = null,
      @Serializable(with = TimeZoneKSerializer::class) val timezone: TimeZone = TimeZone.getDefault()
  ) : Schedule("weekly")

  @Serializable
  class Monthly(
      @field:JsonFormat(pattern = "KK:mm") @Serializable(with = LocalTimeKSerializer::class) val time: LocalTime? = null,
      @Serializable(with = TimeZoneKSerializer::class) val timezone: TimeZone = TimeZone.getDefault()
  ) : Schedule("monthly")
}

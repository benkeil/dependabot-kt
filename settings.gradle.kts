import de.benkeil.dependabotkt.dsl.PackageSystem
import de.benkeil.dependabotkt.dsl.Schedule

pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2/") }
  }
}

plugins {
  id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.5"
  id("io.github.benkeil.dependabotkt") version "1.0.21"
}

rootProject.name = "dependabot-kt"

gitHooks {
  commitMsg { conventionalCommits() }
  preCommit { from { "./gradlew ktfmtCheck" } }
  createHooks(true)
}

dependabot {
  override = true
  registries { gitHubMaven { slug = "benkeil/dependabot-kt" } }
  updates {
    update {
      packageEcosystem = PackageSystem.Gradle
      directory = "/"
      registries { retrieveAllByType() }
      schedule = Schedule.Daily()
      commitMessage {
        prefix = "fix"
        include = "scope"
      }
    }
    update {
      packageEcosystem = PackageSystem.GitHubActions
      directory = "/"
      schedule = Schedule.Daily()
      commitMessage {
        prefix = "[skip ci]"
        include = "scope"
      }
    }
  }
}

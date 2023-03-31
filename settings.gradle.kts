pluginManagement {
  repositories {
    mavenLocal()
    mavenCentral()
    maven { url = uri("https://plugins.gradle.org/m2/") }
    maven {
      url = uri("https://maven.pkg.github.com/benkeil/dependabot-kt")
      credentials {
        username = System.getenv("GITHUB_MVN_REGISTRY_USERNAME")
        password = System.getenv("GITHUB_MVN_REGISTRY_TOKEN")
      }
    }
  }
}

plugins {
  id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.5"
//  id("de.benkeil.dependabotkt") version "1.0.5-dirty-SNAPSHOT"
}

rootProject.name = "dependabotkt"

gitHooks {
  commitMsg { conventionalCommits() }
  preCommit { from { "./gradlew ktfmtCheck" } }
  createHooks(true)
}

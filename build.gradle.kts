import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "de.benkeil"

version = semver.info

plugins {
  kotlin("jvm") version "1.8.10"
  kotlin("plugin.serialization") version "1.8.10"
  id("com.ncorti.ktfmt.gradle") version "0.11.0"
  id("io.wusa.semver-git-plugin") version "2.3.7"
  id("com.gradle.plugin-publish") version "1.1.0"
  id("com.github.johnrengelman.shadow") version "8.1.1"
  id("java-gradle-plugin")
  id("maven-publish")
  id("idea")
}

gradlePlugin {
  website.set("https://github.com/benkeil/dependabot-kt")
  vcsUrl.set("https://github.com/benkeil/dependabot-kt")
  plugins {
    create("dependabotkt") {
      id = "io.github.benkeil.dependabotkt"
      implementationClass = "de.benkeil.dependabotkt.gradle.DependabotPlugin"
      displayName = "Dependabot Plugin"
      description = "Create dependabot.yml configurations with a Kotlin DSL and reusable blocks"
      tags.addAll("github", "dependabot", "kotlin", "dsl")
    }
  }
}

tasks.withType<ShadowJar> { archiveClassifier.set("") }

repositories { mavenCentral() }

publishing {
  repositories {
    maven {
      name = "github"
      url = uri("https://maven.pkg.github.com/benkeil/${project.name}")
      credentials {
        username = System.getenv("GITHUB_MVN_REGISTRY_USERNAME")
        password = System.getenv("GITHUB_MVN_REGISTRY_TOKEN")
      }
    }
  }
  publications {
    register<MavenPublication>("gpr") {
      from(components["java"])
      description = "Create dependabot.yml configurations with a Kotlin DSL and reusable blocks"
      groupId = "$group.dependabotkt"
    }
  }
}

val javaVersion = JavaVersion.VERSION_1_8

dependencies {
  // gradle
  api(gradleApi())
  api(gradleKotlinDsl())
  api(kotlin("stdlib"))

  // kotlin
  implementation("org.jetbrains.kotlin:kotlin-reflect:1.8.0")
  implementation("org.jetbrains.kotlinx:kotlinx-cli:0.3.5")

  // yaml
  implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.14.2")
  implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
  implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.14.2")
  implementation("com.charleskorn.kaml:kaml:0.53.0")

  // test
  testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
  testImplementation("io.kotest:kotest-assertions-core:5.5.5")
  testImplementation("io.kotest:kotest-framework-datatest:5.5.5")
  testImplementation("io.mockk:mockk:1.13.4")
  testImplementation(gradleTestKit())
}

java {
  sourceCompatibility = javaVersion
  targetCompatibility = javaVersion
  withJavadocJar()
  withSourcesJar()
}

tasks.withType<KotlinCompile> {
  with(kotlinOptions) {
    jvmTarget = javaVersion.toString()
    javaParameters = true
    freeCompilerArgs += "-Xcontext-receivers"
  }
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.withType<Javadoc> { isFailOnError = false }

ktfmt {
  maxWidth.set(130)
  removeUnusedImports.set(true)
}

semver {
  snapshotSuffix = "SNAPSHOT" // (default) appended if the commit is without a release tag
  dirtyMarker = "dirty" // (default) appended if there are uncommitted changes
  initialVersion = "0.1.22" // (default) initial version in semantic versioning
  tagPrefix = "v" // (default) each project can have its own tags identified by a unique prefix.
  tagType = io.wusa.TagType.LIGHTWEIGHT // (default) options are Annotated or Lightweight
  branches { // list of branch configurations
    branch {
      regex = ".+" // regex for the branch you want to configure, put this one last
      incrementer = "NO_VERSION_INCREMENTER" // (default) version incrementer
      formatter = Transformer {
        "${semver.info.version.major}.${semver.info.version.minor}.${semver.info.version.patch}"
      }
    }
  }
}

package de.benkeil.dependabotkt.dsl

import de.benkeil.dependabotkt.serialization.toYaml
import io.kotest.core.spec.style.FunSpec
import java.time.DayOfWeek
import java.time.LocalTime
import java.util.TimeZone

class DependabotKtTest :
    FunSpec({
      context("DependabotBuilder") {
        test("dependabot") {
          dependabot {
                registries {
                  gitHubMaven { slug = "benkeil/library" }
                  maven {
                    name = "maven-github"
                    url = "https://maven.pkg.github.com/benkeil/library-2"
                    username = "dependabot"
                    password = expr { SecretsContext.DEPENDABOT_TOKEN }
                    replacesBase = true
                  }
                  docker {
                    name = "docker-github"
                    url = "https://docker.io"
                    username = "dependabot"
                    password = expr { SecretsContext.DEPENDABOT_TOKEN }
                  }
                }
                updates {
                  update {
                    packageEcosystem = PackageSystem.Gradle
                    directory = "/"
                    registries {
                      retrieve("maven-github")
                      retrieve("benkeil/library")
                    }
                    schedule = Schedule.Daily(time = LocalTime.of(9, 0), timezone = TimeZone.getTimeZone("UTC"))
                    commitMessage {
                      prefix = "fix"
                      include = "scope"
                    }
                    ignore {
                      dependency {
                        name = "com.hashicorp:cdktf-provider-vault"
                        versions = "*"
                        type = DependencyContext.UpdateType.Version.Semver.Major
                      }
                      dependency {
                        name = "com.spring:spring-boot"
                        versions = "*"
                        type = DependencyContext.UpdateType.Version.Semver.Minor
                      }
                      dependency {
                        name = "com.spring:spring-boot-aws"
                        versions = "*"
                        type = DependencyContext.UpdateType.Version.Semver.Patch
                      }
                    }
                    openPullRequestsLimit = 20
                    pullRequestBranchName { separator = "-" }
                  }
                  update {
                    packageEcosystem = PackageSystem.GitHubActions
                    directory = "/"
                    schedule =
                        Schedule.Weekly(
                            time = LocalTime.of(9, 0), day = DayOfWeek.WEDNESDAY, timezone = TimeZone.getTimeZone("UTC"))
                    commitMessage {
                      prefix = "[skip ci]"
                      include = "scope"
                    }
                  }
                  update {
                    packageEcosystem = PackageSystem.GitHubActions
                    directory = "/"
                    schedule = Schedule.Monthly(time = LocalTime.of(9, 0), timezone = TimeZone.getTimeZone("UTC"))
                    commitMessage {
                      prefix = "[skip ci]"
                      include = "scope"
                      prefixDevelopment = "foo"
                    }
                  }
                }
              }
              .toYaml()
              .also(::println)
        }

        test("own project") {
          dependabot {
                updates {
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
              .toYaml()
              .also(::println)
        }

        test("retrieve registries") {
          dependabot {
                registries {
                  docker {
                    name = "docker-1"
                    url = "https:docker-1.com"
                  }
                  docker {
                    name = "docker-2"
                    url = "https:docker-2.com"
                  }
                  gitHubMaven { slug = "benkeil/repo-1" }
                  gitHubMaven { slug = "benkeil/repo-2" }
                }
                updates {
                  update {
                    packageEcosystem = PackageSystem.Docker
                    directory = "/"
                    registries { retrieveAllByType() }
                  }
                  update {
                    packageEcosystem = PackageSystem.Gradle
                    directory = "/"
                    registries { retrieveAllByType() }
                  }
                }
              }
              .toYaml()
              .also(::println)
        }

        test("define a service dependabot.yaml") { println(defaultService().toYaml()) }

        test("define a service dependabot.yaml with ignores") {
          defaultService {
                dependency { name = "com.hashicorp:cdktf-provider-vault" }
                dependency { name = "org.springframework.boot" }
                dependency {
                  name = "org.springframework.cloud"
                  type = DependencyContext.UpdateType.Version.Semver.Major
                }
              }
              .toYaml()
              .also(::println)
        }
      }
    })

private fun defaultService(
    repositorySlugs: Collection<String> = listOf("benkeil/library"),
    block: (IgnoreContext.() -> Unit)? = null
) = dependabot {
  registries { repositorySlugs.map { gitHubMaven { slug = it } } }
  updates {
    update {
      packageEcosystem = PackageSystem.Gradle
      directory = "/"
      registries { repositorySlugs.map { retrieve(it) } }
      schedule = Schedule.Daily()
      commitMessage {
        prefix = "fix"
        include = "scope"
      }
      ignore { block?.invoke(this) }
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

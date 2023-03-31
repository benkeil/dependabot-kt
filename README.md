# dependabot-kt

## Example

Use this library to write your own package with reusable dependabot definitions.

```kotlin
fun defaultService(
    repositorySlugs: Collection<String> = listOf("owner/repo"),
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
```

Include your library in your service and use it.

**settings.gradle.kts** in **Service A**

```kotlin
dependabot { defaultService() }
```

**settings.gradle.kts** in **Service B**

```kotlin
dependabot {
  defaultService {
    dependency {
      name = "org.springframework.cloud"
      type = DependencyContext.UpdateType.Version.Semver.Major
    }
  }
}
```

**settings.gradle.kts** in **Service C**

```kotlin
dependabot {
  defaultService {
    dependency { name = "com.hashicorp:cdktf-provider-vault" }
    dependency { name = "org.springframework.boot" }
  }
}
```

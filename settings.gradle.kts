plugins {
  id("org.danilopianini.gradle-pre-commit-git-hooks") version "1.1.1"
}

rootProject.name = "dependabot-kt"

gitHooks {
  commitMsg { conventionalCommits() }
  preCommit { from { "./gradlew ktfmtCheck" } }
  createHooks(true)
}

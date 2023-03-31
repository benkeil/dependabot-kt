package de.benkeil.dependabotkt.dsl

object Contexts {
  val secrets: SecretsContext = SecretsContext
}

object SecretsContext : ExpressionContext("secrets") {

  /**
   * * GITHUB_TOKEN is a secret that is automatically created for every workflow run, and is always included in the secrets
   * context. For more information, see "Automatic token authentication."
   * https://docs.github.com/en/actions/security-guides/automatic-token-authentication
   */
  val DEPENDABOT_TOKEN: String by propertyToExprPath
}

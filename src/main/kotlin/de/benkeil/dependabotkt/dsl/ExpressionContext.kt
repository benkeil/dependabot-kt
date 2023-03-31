package de.benkeil.dependabotkt.dsl

open class ExpressionContext(
    private val path: String,
    val propertyToExprPath: Map<String, String> = MapFromLambda { "$path.$it" },
) : Map<String, String> by propertyToExprPath

internal class MapFromLambda<T>(val operation: (String) -> T) : Map<String, T> by emptyMap() {
  override fun containsKey(key: String) = true
  override fun get(key: String): T = operation(key)
  override fun getOrDefault(key: String, defaultValue: T): T = get(key)
}

internal class FakeList(val name: String) : List<String> by emptyList() {
  override fun get(index: Int): String = "$name[$index]"
}

fun expr(value: String): String = "\${{ ${value.removePrefix("$")} }}"

/**
 * Creates an expression, i.e. something evaluated by GitHub, using type-safe API.
 *
 * https://docs.github.com/en/actions/learn-github-actions/expressions#about-expressions
 * https://docs.github.com/en/actions/learn-github-actions/contexts
 */
fun expr(expression: Contexts.() -> String): String = with(Contexts) { expr(expression()) }

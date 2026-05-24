// port-lint: source kv/source.rs
@file:OptIn(kotlin.experimental.ExperimentalObjCRefinement::class)

package io.github.kotlinmania.log.kv

import kotlin.native.HiddenFromObjC

// Sources for key-values.
//
// This module defines the [Source] interface and supporting APIs for working
// with collections of key-values.

/**
 * A source of key-values.
 *
 * The source may be a single pair, a set of pairs, or a filter over a set of
 * pairs. Use the [VisitSource] interface to inspect the structured data in a
 * source.
 *
 * A source is like an iterator over its key-values, except with a push-based
 * API instead of a pull-based one.
 *
 * # Examples
 *
 * Enumerating the key-values in a source:
 *
 * ```
 * // A VisitSource that prints all key-values.
 * class Printer : VisitSource {
 *     override fun visitPair(key: Key, value: Value): Result<Unit> {
 *         println("$key: $value")
 *         return Result.success(Unit)
 *     }
 * }
 *
 * // A source with 3 key-values.
 * val source = listOf("a" to 1, "b" to 2, "c" to 3).asPairSource()
 *
 * // Visit it.
 * source.visit(Printer()).getOrThrow()
 * ```
 */
public fun interface Source {
    /**
     * Visit key-values.
     *
     * A source doesn't have to guarantee any ordering or uniqueness of
     * key-values. If the given visitor returns an error then the source may
     * early-return with it, even if there are more key-values.
     *
     * # Implementation notes
     *
     * A source should yield the same key-values to a subsequent visitor unless
     * that visitor itself fails.
     */
    public fun visit(visitor: VisitSource): Result<Unit>
}

/**
 * Get the value for a given key.
 *
 * If the key appears multiple times in the source then which key is returned
 * is implementation specific.
 *
 * # Implementation notes
 *
 * A source that can provide a more efficient implementation of this method
 * should override it.
 */
public fun Source.get(key: Key): Value? = getDefault(this, key)

/**
 * Count the number of key-values that can be visited.
 *
 * # Implementation notes
 *
 * A source that knows the number of key-values upfront may provide a more
 * efficient implementation.
 *
 * A subsequent call to [visit] should yield the same number of key-values.
 */
public fun Source.count(): Int = countDefault(this)

/**
 * The internal visitor used by [getDefault] to find the first value for a
 * matching key. Promoted from an anonymous object so the upstream `Get`
 * helper struct keeps a named counterpart in the Kotlin port.
 */
private class Get(
    val target: Key,
    var found: Value? = null,
) : VisitSource {
    override fun visitPair(key: Key, value: Value): Result<Unit> {
        if (target == key) {
            found = value
        }
        return Result.success(Unit)
    }
}

/**
 * The internal visitor used by [countDefault] to tally key-value pairs.
 * Promoted from an anonymous object so the upstream `Count` helper struct
 * keeps a named counterpart in the Kotlin port.
 */
private class Count(
    var value: Int = 0,
) : VisitSource {
    override fun visitPair(key: Key, value: Value): Result<Unit> {
        this.value += 1
        return Result.success(Unit)
    }
}

/**
 * The default implementation of [Source.get].
 */
private fun getDefault(source: Source, key: Key): Value? {
    val get = Get(key)
    source.visit(get)
    return get.found
}

/**
 * The default implementation of [Source.count].
 */
private fun countDefault(source: Source): Int {
    val count = Count()
    source.visit(count)
    return count.value
}

/**
 * A visitor for the key-value pairs in a [Source].
 */
public interface VisitSource {
    /**
     * Visit a key-value pair.
     */
    public fun visitPair(key: Key, value: Value): Result<Unit>
}

/**
 * Alias for [VisitSource] preserving the upstream legacy `Visitor` name. The
 * upstream Rust crate re-exports VisitSource under the name Visitor when the
 * kv_unstable feature is enabled, and marks it deprecated; the Kotlin
 * counterpart is also deprecated.
 */
@Deprecated(
    message = "Use VisitSource directly.",
    replaceWith = ReplaceWith("VisitSource"),
    level = DeprecationLevel.WARNING,
)
public typealias Visitor = VisitSource

/**
 * Hidden from Swift Export: a public `Pair<K, V>` receiver drags the
 * `kotlin.Pair` stdlib bridge into the generated Swift module, where the
 * unchecked-cast warnings on the `Pair` accessors fail `-Werror`. Kotlin
 * callers continue to use `pair.asSource()` directly.
 */
@HiddenFromObjC
public fun <K, V> Pair<K, V>.asSource(): Source
    where
        K : ToKey,
        V : ToValue =
    Source { visitor -> visitor.visitPair(first.toKey(), second.toValue()) }

/**
 * Hidden from Swift Export: a public generic `List<S>` receiver bridges
 * `kotlin.collections.List` with unchecked-cast warnings that fail
 * `-Werror`. Kotlin callers continue to use `list.asSource()` directly.
 */
@HiddenFromObjC
public fun <S : Source> List<S>.asSource(): Source =
    Source { visitor ->
        for (source in this) {
            source.visit(visitor).getOrElse { return@Source Result.failure(it) }
        }
        Result.success(Unit)
    }

/**
 * Hidden from Swift Export: a public generic `Array<S>` receiver bridges
 * the `kotlin.Array` stdlib surface and emits unchecked-cast warnings that
 * fail `-Werror`. Kotlin callers continue to use `array.asSource()`.
 */
@HiddenFromObjC
public fun <S : Source> Array<S>.asSource(): Source =
    Source { visitor ->
        for (source in this) {
            source.visit(visitor).getOrElse { return@Source Result.failure(it) }
        }
        Result.success(Unit)
    }

/**
 * Hidden from Swift Export: a nullable generic receiver drags the
 * stdlib generics bridge and emits unchecked-cast warnings that fail
 * `-Werror`. Kotlin callers continue to use `nullable.asSource()`.
 */
@HiddenFromObjC
public fun <S : Source> S?.asSource(): Source =
    Source { visitor ->
        val source = this ?: return@Source Result.success(Unit)
        source.visit(visitor)
    }

internal fun <K, V> Map<K, V>.asSource(): Source
    where
        K : ToKey,
        V : ToValue =
    Source { visitor ->
        for ((key, value) in this) {
            visitor.visitPair(key.toKey(), value.toValue()).getOrElse { return@Source Result.failure(it) }
        }
        Result.success(Unit)
    }

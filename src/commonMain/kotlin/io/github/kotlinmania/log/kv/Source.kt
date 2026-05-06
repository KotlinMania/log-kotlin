// port-lint: source src/kv/source.rs
package io.github.kotlinmania.log.kv

/**
 * A source of key-values.
 *
 * The source may be a single pair, a set of pairs, or a filter over a set of pairs.
 * Use the [VisitSource] interface to inspect the structured data in a source.
 *
 * A source is like an iterator over its key-values, except with a push-based API
 * instead of a pull-based one.
 *
 * # Examples
 *
 * Enumerating the key-values in a source:
 *
 * ```
 * // A VisitSource that prints all key-values.
 * class Printer : VisitSource {
 *     override fun visitPair(key: Key, value: Value): Result<Unit> {
 *         println(\"$key: $value\")
 *         return Result.success(Unit)
 *     }
 * }
 *
 * // A source with 3 key-values.
 * val source = listOf(Pair(\"a\", 1), Pair(\"b\", 2), Pair(\"c\", 3))
 *
 * // Visit it.
 * source.asSource().visit(Printer()).getOrThrow()
 * ```
 */
public fun interface Source {
    /**
     * Visit key-values.
     *
     * A source doesn't have to guarantee any ordering or uniqueness of key-values.
     * If the given visitor returns an error then the source may early-return with it,
     * even if there are more key-values.
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
 * The default implementation of [Source.get].
 */
private fun getDefault(source: Source, key: Key): Value? {
    val targetKey = key
    var found: Value? = null
    val visitor =
        object : VisitSource {
            override fun visitPair(key: Key, value: Value): Result<Unit> {
                if (targetKey == key) {
                    found = value
                }
                return Result.success(Unit)
            }
        }

    source.visit(visitor)
    return found
}

/**
 * The default implementation of [Source.count].
 */
private fun countDefault(source: Source): Int {
    var count = 0
    val visitor =
        object : VisitSource {
            override fun visitPair(key: Key, value: Value): Result<Unit> {
                count += 1
                return Result.success(Unit)
            }
        }

    source.visit(visitor)
    return count
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

public fun <K, V> Pair<K, V>.asSource(): Source
    where
        K : ToKey,
        V : ToValue =
    Source { visitor -> visitor.visitPair(first.toKey(), second.toValue()) }

public fun <S : Source> List<S>.asSource(): Source =
    Source { visitor ->
        for (source in this) {
            source.visit(visitor).getOrElse { return@Source Result.failure(it) }
        }
        Result.success(Unit)
    }

public fun <S : Source> Array<S>.asSource(): Source =
    Source { visitor ->
        for (source in this) {
            source.visit(visitor).getOrElse { return@Source Result.failure(it) }
        }
        Result.success(Unit)
    }

public fun <S : Source> S?.asSource(): Source =
    Source { visitor ->
        val source = this ?: return@Source Result.success(Unit)
        source.visit(visitor)
    }

public fun <K, V> Map<K, V>.asSource(): Source
    where
        K : ToKey,
        V : ToValue =
    Source { visitor ->
        for ((key, value) in this) {
            visitor.visitPair(key.toKey(), value.toValue()).getOrElse { return@Source Result.failure(it) }
        }
        Result.success(Unit)
    }

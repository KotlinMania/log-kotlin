// port-lint: source kv/mod.rs
package io.github.kotlinmania.log.kv

/**
 * Structured logging.
 *
 * # Structured logging
 *
 * Structured logging enhances traditional text-based log records with user-defined
 * attributes. Structured logs can be analyzed using a variety of data processing
 * techniques, without needing to find and parse attributes from unstructured text first.
 *
 * User-defined attributes are part of a [Source] on the log record. Each attribute is a
 * key-value: a pair of [Key] and [Value]. Keys are strings and values are a datum of any
 * type that can be formatted or serialized. Simple types like strings, booleans, and
 * numbers are supported, as well as arbitrarily complex structures involving nested
 * objects and sequences.
 *
 * ## Adding key-values to log records
 *
 * Key-values are passed alongside the message format when invoking the log helpers:
 *
 * ```
 * // info(formatArgs("Something of interest"), kv("a", 1.toValue()))
 * ```
 *
 * Helpers in [Macros][io.github.kotlinmania.log] capture values through the [ToValue]
 * functional interface by default. To capture a value using a different conversion, use a
 * dedicated builder after the key. Here's how the same example can capture `a` using its
 * debug formatting instead:
 *
 * ```
 * // info(formatArgs("Something of interest"), kvDebug("a", 1))
 * ```
 *
 * The following capturing helpers are supported:
 *
 * - [kvDebug][io.github.kotlinmania.log.kvDebug] captures the value using debug formatting.
 * - [kvDisplay][io.github.kotlinmania.log.kvDisplay] captures the value using display formatting.
 * - [captureError][io.github.kotlinmania.log.captureError] captures the value as a [Throwable].
 * - [captureSval][io.github.kotlinmania.log.captureSval] captures the value through the `sval` data model.
 * - [captureSerde][io.github.kotlinmania.log.captureSerde] captures the value through serde-shaped serialization.
 *
 * ## Working with key-values on log records
 *
 * Use the `Record.keyValues` method to access key-values.
 *
 * Individual values can be pulled from the source by their key:
 *
 * ```
 * // val record = Record.builder().keyValues(listOf(Pair("a", 1)).asSource()).build()
 * //
 * // val a: Value = record.keyValues().get(Key.fromStr("a"))!!
 * // kotlin.test.assertEquals(1L, a.toI64())
 * ```
 *
 * All key-values can also be enumerated using a [VisitSource]:
 *
 * ```
 * // class Collect(val collected: MutableMap<Key, Value>) : VisitSource {
 * //     override fun visitPair(key: Key, value: Value): Result<Unit> {
 * //         collected[key] = value
 * //         return Result.success(Unit)
 * //     }
 * // }
 * //
 * // val visitor = Collect(mutableMapOf())
 * // record.keyValues().visit(visitor).getOrThrow()
 * // kotlin.test.assertEquals(listOf("a", "b", "c"), visitor.collected.keys.map { it.asStr() })
 * ```
 *
 * [Value]s have methods for conversions to common types:
 *
 * ```
 * // val a = record.keyValues().get(Key.fromStr("a"))!!
 * // kotlin.test.assertEquals(1L, a.toI64())
 * ```
 *
 * Values also have their own [VisitValue] type. Value visitors are a lightweight
 * API for working with primitive types:
 *
 * ```
 * // class IsNumeric(var isNumeric: Boolean) : VisitValue {
 * //     override fun visitAny(value: Value): Result<Unit> {
 * //         isNumeric = false
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitU64(value: ULong): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitI64(value: Long): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitU128(value: UInt128): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitI128(value: Int128): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * //
 * //     override fun visitF64(value: Double): Result<Unit> {
 * //         isNumeric = true
 * //         return Result.success(Unit)
 * //     }
 * // }
 * //
 * // val a = record.keyValues().get(Key.fromStr("a"))!!
 * // val visitor = IsNumeric(false)
 * // a.visit(visitor).getOrThrow()
 * // kotlin.test.assertTrue(visitor.isNumeric)
 * ```
 *
 * To serialize a value to a format like JSON, you can also use either `serde` or `sval`.
 *
 * The choice of serialization framework depends on the needs of the consumer.
 * Log producers and log consumers don't need to agree on the serialization framework.
 * A value can be captured using serde serialization and still be serialized
 * through `sval` without losing any structure or data.
 *
 * Values can also always be formatted using debug and display formatting:
 *
 * ```
 * // data class Data(val a: Int, val b: Boolean, val c: String)
 * // val data = Data(a = 1, b = true, c = "Some data")
 * // val a = Value.fromDebug(data)
 * // kotlin.test.assertEquals("Data(a=1, b=true, c=Some data)", a.toString())
 * ```
 */
// Tracking file for the upstream module aggregator. The upstream visitor type, exposed at
// the package root by an upstream re-export under the name [Visitor], is provided in this
// package by [VisitSource]. Callers in other modules reference [VisitSource] directly, or
// alias it with `import io.github.kotlinmania.log.kv.VisitSource as Visitor` when an
// upstream-shaped spelling is required.
//
// Callers migrated:
//   (none — workspace audit confirmed zero Kotlin callers held a direct or wildcard import
//    of the re-exported identifier at the time this ledger was opened.)
